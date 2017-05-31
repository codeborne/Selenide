package integration;

import com.automation.remarks.video.annotations.Video;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class TabsTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_tabs.html");
  }

  @Test @Video
  public void userCanBrowseTabs_webdriver_api() {
    openFile("page_with_tabs.html");

    WebDriver driver = WebDriverRunner.getWebDriver();

    $(byText("Page1: uploads")).click();
    Wait().until(ExpectedConditions.numberOfWindowsToBe(2));

    $("h1").shouldHave(text("Tabs"));

    String windowHandle = driver.getWindowHandle();
    
    driver.switchTo().window(nextWindowHandle(driver));
    $("h1").shouldHave(text("File uploads"));

    driver.switchTo().window(windowHandle);
    $("h1").shouldHave(text("Tabs"));
  }

  private String nextWindowHandle(WebDriver driver) {
    String windowHandle = driver.getWindowHandle();
    Set<String> windowHandles = driver.getWindowHandles();
    windowHandles.remove(windowHandle);

    return windowHandles.iterator().next();
  }

  @Test @Video
  public void canSwitchToWindowByTitle() {
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));
    switchTo().window("Test::alerts"); $("h1").shouldHave(text("Page with alerts"));
    switchTo().window("Test::jquery"); $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window("Test::uploads"); $("h1").shouldHave(text("File uploads"));
    switchTo().window("Test::tabs"); $("h1").shouldHave(text("Tabs"));
  }

  @Test @Video
  public void canSwitchToWindowByIndex_chrome() {
    assumeTrue(isChrome());
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));

    switchTo().window(1); $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window(2); $("h1").shouldHave(text("File uploads"));
    switchTo().window(3); $("h1").shouldHave(text("Page with alerts"));
    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
  }

  @Test @Video
  public void canSwitchToWindowByIndex_other_browsers_but_htmlunit() {
    assumeFalse(isChrome() || isHtmlUnit());
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));

    switchTo().window(1); $("h1").shouldHave(text("Page with alerts"));
    switchTo().window(2); $("h1").shouldHave(text("File uploads"));
    switchTo().window(3); $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
  }

  @Test @Video
  public void canSwitchBetweenWindowsWithSameTitles() {
    assumeFalse(isHtmlUnit());
    $(byText("Page4: same title")).click();
    $("h1").shouldHave(text("Tabs"));

    switchTo().window("Test::tabs::title"); $("body").shouldHave(text("Secret phrase 1"));
    String firstHandle = getWebDriver().getWindowHandle();

    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
    $(byText("Page5: same title")).click();
    switchTo().window("Test::tabs::title"); $("body").shouldHave(text("Secret phrase 1"));
    switchTo().window(0); $("h1").shouldHave(text("Tabs"));

//    switchTo().windowExceptHandles("Test::tabs::title", firstHandle); $("body").shouldHave(text("Secret phrase 2"));
//    String secondHandle = getWebDriver().getWindowHandle();
//
//    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
//    $(byText("Page6: same title")).click();
//    switchTo().window("Test::tabs::title"); $("body").shouldHave(text("Secret phrase 1")); 
//    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
//
//    switchTo().windowExceptHandles("Test::tabs::title", firstHandle, secondHandle);
//    $("body").shouldHave(text("Secret phrase 3"));
//
//    switchTo().window("Test::tabs"); $("h1").shouldHave(text("Tabs"));
  }
  
  @After
  public void tearDown() {
    close();
  }
}
