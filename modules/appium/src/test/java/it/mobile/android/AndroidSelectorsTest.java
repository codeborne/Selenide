package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumSelectors.byAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.byContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndText;
import static com.codeborne.selenide.appium.AppiumSelectors.byText;
import static com.codeborne.selenide.appium.AppiumSelectors.withAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.withContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndText;
import static com.codeborne.selenide.appium.AppiumSelectors.withText;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.back;
import static com.codeborne.selenide.appium.selector.CombinedBy.android;

class AndroidSelectorsTest extends BaseApiDemosTest {

  private static final String VIEWS = "Views";

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void appiumSelectorsInAndroidApp() {
    $(byAttribute("content-desc", VIEWS)).click();
    back();
    $(byContentDescription(VIEWS)).click();
    back();
    $(byTagAndAttribute("*", "text", VIEWS)).click();
    back();
    $(byTagAndContentDescription("*", VIEWS)).click();
    back();
    $(byTagAndText("*", VIEWS)).click();
    back();
    $(byText(VIEWS)).click();
    back();
    $(withAttribute("text", "Graphi")).click();
    back();
    $(withContentDescription("Graphi")).click();
    back();
    $(withTagAndAttribute("*", "text", "Graphi")).click();
    back();
    $(withTagAndContentDescription("*", "Graphi")).click();
    back();
    $(withTagAndText("android.widget.TextView", "Graphi")).click();
    back();
    $(android(withText("Graphi")).ios(By.xpath("/"))).shouldHave(text("Graphics"));
    $(android(byText("Graphics")).ios(By.xpath("/"))).shouldHave(text("Graphics"));
  }
}
