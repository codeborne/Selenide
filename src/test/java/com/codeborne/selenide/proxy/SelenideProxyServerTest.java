package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Configuration;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelenideProxyServerTest implements WithAssertions {
  private BrowserMobProxyServer bmp = mock(BrowserMobProxyServer.class);
  private SelenideProxyServer proxyServer = new SelenideProxyServer(null, new InetAddressResolverStub(), bmp);

  @BeforeEach
  @AfterEach
  void resetProxySettings() {
    Configuration.proxyPort = 0;
    Configuration.proxyHost = "";
  }

  @Test
  void canInterceptResponses() {
    proxyServer.start();

    verify(bmp).setTrustAllServers(true);
    verify(bmp, never()).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp).start(0);

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    assertThat(filter.getDownloadedFiles()).hasSize(0);
  }

  @Test
  void canShutdownProxyServer() {
    proxyServer.shutdown();
    verify(bmp).abort();
  }

  @Test
  void createSeleniumProxy() {
    when(bmp.getPort()).thenReturn(8888);

    assertThat(proxyServer.createSeleniumProxy().getHttpProxy()).endsWith(":8888");
  }

  @Test
  void createSeleniumProxy_withConfiguredHostname() {
    Configuration.proxyHost = "my.megahost";
    when(bmp.getPort()).thenReturn(9999);
    assertThat(proxyServer.createSeleniumProxy().getHttpProxy()).isEqualTo("my.megahost:9999");
  }

  @Test
  void extractsProxyAddress() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("111.22.3.4444:8080");

    InetSocketAddress proxyAddress = SelenideProxyServer.getProxyAddress(proxy);

    assertThat(proxyAddress.getHostName()).isEqualTo("111.22.3.4444");
    assertThat(proxyAddress.getPort()).isEqualTo(8080);
  }

  @Test
  void canStartProxyServerOnConfiguredPort() {
    Configuration.proxyPort = 666;

    proxyServer.start();

    verify(bmp).start(666);
  }
}
