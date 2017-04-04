package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import com.codeborne.selenide.junit.TextReport;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

import java.util.Locale;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  static {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %5$s%6$s%n"); // add %2$s for source
    Locale.setDefault(Locale.ENGLISH);
  }

  private static final Logger log = Logger.getLogger(IntegrationTest.class.getName());
  // http or https
  private static final boolean SSL = false;
  private static String protocol;

  @Rule
  public ScreenShooter img = ScreenShooter.failedTests();

  @Rule
  public TestRule report = new TextReport().onFailedTest(true).onSucceededTest(true);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static int port;
  protected static LocalHttpServer server;
  private long defaultTimeout;
  protected static long averageSeleniumCommandDuration = 100;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port, SSL).start();
        if (SSL) {
          protocol = "https://";
        } else {
          protocol = "http://";
        }
        log.info("START " + browser + " TESTS");
        Configuration.baseUrl = protocol + "127.0.0.1:" + port;
      }
    }
  }

  @Before
  public void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  @Before
  public void resetSettings() {
    Configuration.baseUrl = protocol + "127.0.0.1:" + port;
    Configuration.reportsFolder = "build/reports/tests/" + Configuration.browser;
    fastSetValue = false;
    browserSize = "1024x768";
    server.uploadedFiles.clear();
    Configuration.fileDownload = PROXY;
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    open("/" + fileName + "?browser=" + Configuration.browser +
        "&timeout=" + Configuration.timeout);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + Configuration.browser +
        "&timeout=" + Configuration.timeout, pageObjectClass);
  }

  @Before
  public final void rememberTimeout() {
    defaultTimeout = timeout;
  }

  @After
  public final void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }
}
