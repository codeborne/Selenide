package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public class ErrorMessagesForMissingElementTest extends IntegrationTest {
  private PageObject pageObject;


  @Before
  public final void setTimeout() {
    timeout = 1500;
    pageObject = openFile("page_with_selects_without_jquery.html", PageObject.class);
  }

  private String reportsUrl;

  @Before
  public void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    Configuration.reportsUrl = "http://ci.org/";
    Screenshots.screenshots = new ScreenShotLaboratory() {
      @Override
      public String takeScreenShot() {
        return new File(reportsFolder, "1.jpg").getAbsolutePath();
      }
    };
  }

  @After
  public void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Screenshots.screenshots = new ScreenShotLaboratory();
  }

  @Test
  public void elementNotFound() {
    try {
      $("h9").shouldHave(text("expected text"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertStartsWith("Element not found {h9}\n" +
          "Expected: text 'expected text'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.\n" +
          "Caused by: NoSuchElementException:", expected);
      assertEquals("http://ci.org/build/reports/tests/" + browser + "/1.jpg", expected.getScreenshot());
    }
  }

  @Test
  public void elementTextDoesNotMatch() {
    try {
      $("h2").shouldHave(text("expected text"));
      fail("Expected ElementShould");
    } catch (ElementShould expected) {
      assertEquals("Element should have text 'expected text' {h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", expected.toString());
      assertEquals("http://ci.org/build/reports/tests/" + browser + "/1.jpg", expected.getScreenshot());
    }
  }

  @Test
  public void elementAttributeDoesNotMatch() {
    try {
      $("h2").shouldHave(attribute("name", "header"));
      fail("Expected ElementShould");
    } catch (ElementShould expected) {
      assertEquals("Element should have attribute name=header {h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", expected.toString());
    }
  }


  @Test
  public void wrapperTextDoesNotMatch() {
    try {
      $(getElement(By.tagName("h2"))).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    } catch (ElementShould expected) {
      assertEquals("Element should have text 'expected text' {By.tagName: h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", expected.toString());
    }
  }

  @Test
  public void clickHiddenElement() {
    try {
      $("#theHiddenElement").click();
      fail("Expected ElementShould");
    } catch (ElementShould elementShouldExist) {
      assertEquals("Element should be visible {#theHiddenElement}\n" +
          "Element: '<div id=\"theHiddenElement\" displayed:false></div>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", elementShouldExist.toString());
      assertEquals("http://ci.org/build/reports/tests/" + browser + "/1.jpg", elementShouldExist.getScreenshot());
    }
  }

  @Test
  public void pageObjectElementTextDoesNotMatch() {
    try {
      $(pageObject.header1).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    } catch (ElementShould expected) {
      assertEquals("Element should have text 'expected text' {By.tagName: h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", expected.toString());
    }
  }

  @Test
  public void pageObjectWrapperTextDoesNotMatch() {
    try {
      $(pageObject.header2).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    } catch (ElementShould expected) {
      assertEquals("Element should have text 'expected text' {By.tagName: h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", expected.toString());
    }
  }

  @Test
  public void selectOptionFromUnexistingList() {
    try {
      $(pageObject.categoryDropdown).selectOption("SomeOption");
    } catch (ElementNotFound e) {
      assertContains(e, "Element not found {By.id: invalid_id}", "Expected: exist");
    }
  }

  @Test
  public void clickUnexistingWrappedElement() {
    try {
      $(pageObject.categoryDropdown).click();
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertStartsWith("Element not found {By.id: invalid_id}\n" +
          "Expected: visible\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.\n" +
          "Caused by: NoSuchElementException:", e);
    }
  }

  private void assertStartsWith(String expectedMessageStart, Error error) {
    assertTrue("Error should start with " + expectedMessageStart + ", but received: " + error,
        error.toString().startsWith(expectedMessageStart));
  }

  @Test
  public void existingElementShouldNotExist() {
    try {
      $("h2").shouldNot(exist);
      fail("Expected ElementFound");
    } catch (ElementShouldNot e) {
      assertEquals("Element should not exist {h2}\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.", e.toString());
    }
  }

  @Test
  public void nonExistingElementShouldNotBeHidden() {
    try {
      $("h14").shouldNotBe(hidden);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertStartsWith("Element not found {h14}\n" +
          "Expected: not hidden\n" +
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n" +
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n" +
          "Timeout: 1.500 s.\n" +
          "Caused by: NoSuchElementException:", e);
    }
  }

  @Test
  public void clickingNonClickableElement() {
    assumeTrue(WebDriverRunner.isChrome());

    try {
      $("#non-clickable-element a").shouldBe(visible).click();
      fail("Expected WebDriverException");
    } catch (UIAssertionError e) {
      assertContains(e, "is not clickable at point",
          "Other element would receive the click",
          "Screenshot: http://ci.org/build/reports/tests/" + browser + "/1.jpg\n",
          "Page source: http://ci.org/build/reports/tests/" + browser + "/1.html\n");
    }
  }

  private void assertContains(UIAssertionError e, String... expectedTexts) {
    for (String expectedText : expectedTexts) {
      assertTrue("Text not found: " + expectedText + " in error message: " + e,
          e.toString().contains(expectedText));
    }
  }

  public static class PageObject {
    @FindBy(tagName = "h2")
    public SelenideElement header1;

    @FindBy(tagName = "h2")
    public WebElement header2;

    @FindBy(id = "invalid_id")
    private WebElement categoryDropdown;
  }
}
