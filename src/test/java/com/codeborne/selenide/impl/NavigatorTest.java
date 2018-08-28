package com.codeborne.selenide.impl;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.extension.MockWebDriverExtension;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockWebDriverExtension.class)
class NavigatorTest implements WithAssertions {
  private Navigator navigator = new Navigator();
  WebDriver driver = mock(WebDriver.class);
  WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
  SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
  AuthenticationFilter authenticationFilter = mock(AuthenticationFilter.class);

  @BeforeEach
  void setUp() {
    Configuration.fileDownload = HTTPGET;
    WebDriverRunner.webdriverContainer = mock(WebDriverThreadLocalContainer.class);
    doReturn(driver).when(WebDriverRunner.webdriverContainer).getAndCheckWebDriver();
    doReturn(selenideProxy).when(WebDriverRunner.webdriverContainer).getProxyServer();
    doReturn(navigation).when(driver).navigate();
    doReturn(authenticationFilter).when(selenideProxy).requestFilter("authentication");
  }

  @Test
  void detectsAbsoluteUrls() {
    assertThat(navigator.isAbsoluteUrl("http://selenide.org"))
      .as("protocol http")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("https://selenide.org"))
      .as("protocol https")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("file:///tmp/memory.dump"))
      .as("protocol file")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("HTTP://SELENIDE.ORG"))
      .as("case insensitive: HTTP")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("HTTPS://SELENIDE.ORG"))
      .as("case insensitive: HTTPS")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("FILE:///TMP/MEMORY.DUMP"))
      .as("case insensitive: FILE")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("/tmp/memory.dump"))
      .as("relative url")
      .isFalse();
    assertThat(navigator.isAbsoluteUrl("/payments/history"))
      .as("relative url")
      .isFalse();
  }

  @Test
  void returnsAbsoluteUrl() {
    Configuration.baseUrl = "http://localhost:8080";
    assertThat(navigator.absoluteUrl("/users/id=1")).isEqualTo("http://localhost:8080/users/id=1");
    assertThat(navigator.absoluteUrl("http://host:port/users/id=1")).isEqualTo("http://host:port/users/id=1");
  }

  @Test
  void open_withoutAuthentication() {
    navigator.open("https://some.com/login");

    verify(navigation).to("https://some.com/login");
  }

  @Test
  void open_addsEventToLog() {
    LogEventListener listener = mock(LogEventListener.class);
    SelenideLogger.addListener("listener-01", listener);

    navigator.open("https://some.com/login");

    verify(navigation).to("https://some.com/login");
    verify(listener).onEvent(argThat(log ->
      "open".equals(log.getElement()) && "https://some.com/login".equals(log.getSubject())));
  }

  @Test
  void open_withoutAuthentication_resetsPreviousAuthentication() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = true;

    navigator.open("https://some.com/login");

    verify(navigation).to("https://some.com/login");
    verify(authenticationFilter).removeAuthentication();
  }

  @Test
  void open_withBasicAuth_noProxy() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = false;

    navigator.open("https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    verify(navigation).to("https://basic-auth-login:basic-auth-password@some.com/login");
  }

  @Test
  void open_withBasicAuth_withProxy() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = true;

    navigator.open("https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    verify(navigation).to("https://some.com/login");
    verify(authenticationFilter)
      .setAuthentication(eq(AuthenticationType.BASIC), refEq(new Credentials("basic-auth-login", "basic-auth-password")));
  }

  @Test
  void startsProxyServer_evenIfProxyIsNotEnabled_butFileDownloadModeIsProxy() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = PROXY;

    navigator.open("https://some.com/login");

    assertThat(Configuration.proxyEnabled).isTrue();
  }
}
