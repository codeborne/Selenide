package it.mobile;

import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;

class SelenideAppiumPageFactoryTest {

  @Test
  void exception_on_init_mobile_element_without_webdriver() {
    Selenide.closeWebDriver();
    assertThatThrownBy(PageWithPlatformSelectors::new)
      .isInstanceOf(WebDriverException.class)
        .hasMessageStartingWith("The Appium Page factory requires a webdriver instance to be created before page initialization;" +
        " No webdriver is bound to current thread. You need to call open() first");
  }

  @Test
  void web_element_page_factory_doesnt_require_webdriver_instance() {
    Selenide.closeWebDriver();
    var page = new PageWithWebSelectors();
    assertThat(page.element).isNotNull()
      .hasToString("{By.id: someId}");
  }

  @Test
  void mobile_platform_element_successfully_init_with_created_webdriver() {
    open();
    var page = new PageWithPlatformSelectors();
    assertThat(page.element).isNotNull()
      .hasToString("{By.id: element}");
  }

  private static class PageWithPlatformSelectors {
    @AndroidFindBy(id = "element")
    public SelenideElement element;
  }

  private static class PageWithWebSelectors {
    @FindBy(id = "someId")
    public SelenideElement element;
  }
}
