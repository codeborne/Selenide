package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BrowserDownloadsFolder;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.impl.FileNamer;
import com.codeborne.selenide.impl.Plugins;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.proxy.SelenideProxyServerFactory;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.net.HostIdentifier;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

import static com.codeborne.selenide.impl.FileHelper.deleteFolderIfEmpty;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.lang.Thread.currentThread;

@ParametersAreNonnullByDefault
public class CreateDriverCommand {
  private static final Logger log = LoggerFactory.getLogger(CreateDriverCommand.class);
  private final FileNamer fileNamer;

  public CreateDriverCommand() {
    this(new FileNamer());
  }

  CreateDriverCommand(FileNamer fileNamer) {
    this.fileNamer = fileNamer;
  }

  @Nonnull
  public Result createDriver(Config config,
                             WebDriverFactory factory,
                             @Nullable Proxy userProvidedProxy,
                             List<WebDriverEventListener> eventListeners,
                             List<WebDriverListener> listeners) {
    if (!config.reopenBrowserOnFail()) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
        ", and cannot create a new webdriver because reopenBrowserOnFail=false");
    }

    log.debug("Creating webdriver in thread {} (ip: {}, host: {})...",
      currentThread().getId(), HostIdentifier.getHostName(), HostIdentifier.getHostAddress());

    SelenideProxyServer selenideProxyServer = null;
    Proxy browserProxy = userProvidedProxy;

    if (config.proxyEnabled()) {
      SelenideProxyServerFactory selenideProxyServerFactory = Plugins.inject(SelenideProxyServerFactory.class);
      try {
        selenideProxyServer = selenideProxyServerFactory.create(config, userProvidedProxy);
        browserProxy = selenideProxyServer.getSeleniumProxy();
      }
      catch (NoClassDefFoundError e) {
        throw new IllegalStateException("Cannot initialize proxy. " +
          "Probably you should add BrowserUpProxy dependency to your project " +
          "- see https://search.maven.org/search?q=a:browserup-proxy-core", e);
      }
    }

    @Nullable File browserDownloadsFolder = config.remote() != null ? null :
      ensureFolderExists(new File(config.downloadsFolder(), fileNamer.generateFileName()).getAbsoluteFile());

    WebDriver webdriver = factory.createWebDriver(config, browserProxy, browserDownloadsFolder);

    log.info("Created webdriver in thread {}: {} -> {}",
      currentThread().getId(), webdriver.getClass().getSimpleName(), webdriver);

    WebDriver webDriver = addListeners(webdriver, eventListeners, listeners);
    Runtime.getRuntime().addShutdownHook(
      new Thread(new SelenideDriverFinalCleanupThread(config, webDriver, selenideProxyServer))
    );
    if (browserDownloadsFolder != null) {
      Runtime.getRuntime().addShutdownHook(
        new Thread(() -> deleteFolderIfEmpty(browserDownloadsFolder))
      );
    }
    return new Result(webDriver, selenideProxyServer, BrowserDownloadsFolder.from(browserDownloadsFolder));
  }

  @Nonnull
  private WebDriver addListeners(WebDriver webdriver,
                                 List<WebDriverEventListener> eventListeners,
                                 List<WebDriverListener> listeners) {
    return addWebDriverListeners(
      addEventListeners(webdriver, eventListeners),
      listeners
    );
  }

  @Nonnull
  private WebDriver addEventListeners(WebDriver webdriver, List<WebDriverEventListener> eventListeners) {
    if (eventListeners.isEmpty()) {
      return webdriver;
    }
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : eventListeners) {
      log.info("Add listener to webdriver: {}", listener);
      wrapper.register(listener);
    }
    return wrapper;
  }

  @Nonnull
  private WebDriver addWebDriverListeners(WebDriver webdriver, List<WebDriverListener> listeners) {
    if (listeners.isEmpty()) {
      return webdriver;
    }

    log.info("Add listeners to webdriver: {}", listeners);
    EventFiringDecorator wrapper = new EventFiringDecorator(listeners.toArray(new WebDriverListener[]{}));
    return wrapper.decorate(webdriver);
  }

  @ParametersAreNonnullByDefault
  public static class Result {
    public final WebDriver webDriver;
    @Nullable
    public final SelenideProxyServer selenideProxyServer;
    @Nullable
    public final DownloadsFolder browserDownloadsFolder;

    public Result(WebDriver webDriver,
                  @Nullable SelenideProxyServer selenideProxyServer,
                  @Nullable DownloadsFolder browserDownloadsFolder) {
      this.webDriver = webDriver;
      this.selenideProxyServer = selenideProxyServer;
      this.browserDownloadsFolder = browserDownloadsFolder;
    }
  }
}
