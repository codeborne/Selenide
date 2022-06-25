package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static integration.ScreenshotTestHelper.verifyScreenshotSize;
import static org.assertj.core.api.Assumptions.assumeThat;

public class FullScreenshotsGridTest extends AbstractGridTest {
  private static final int width = 2200;
  private static final int height = 3300;

  @BeforeAll
  static void beforeAll() {
    assumeThat(isEdge()).as("Edge throws 'unknown command: session/*/goog/cdp/execute'").isFalse();
  }

  @BeforeEach
  void setUp() {
    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
  }

  /*
     In non-local browser (grid),
     It fails or takes a screenshot of the wrong tab.
     See https://github.com/SeleniumHQ/selenium/issues/10810
     */
  @Test
  void canTakeFullScreenshotWithTwoTabs() throws IOException {
    openFile("page_of_fixed_size_2200x3300.html");
    webdriver().shouldHave(title("Test::page of fixed size 2200x3300"));

    Selenide.executeJavaScript("window.open()");
    Selenide.switchTo().window(1);
    openFile("file_upload_form.html");

    Selenide.switchTo().window(0);

    File screenshot = Selenide.screenshot(OutputType.FILE);
    verifyScreenshotSize(screenshot, width, height);
  }
}
