package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TabsCustomWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_tabs_with_big_delays.html");
  }

  @Test
  void waitsUntilTabAppears_withCustomTimeout() {
    $("#open-new-tab-with-delay").click();
    switchTo().window("Test::alerts", Duration.ofSeconds(5));
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  void waitsUntilTabAppears_withoutCustomTimeout() {
    $("#open-new-tab-with-delay").click();
    assertThatThrownBy(() -> switchTo().window(1))
      .isInstanceOf(NoSuchWindowException.class);
  }

  @Test
  void waitsUntilTabAppears_withLowerTimeout() {
    $("#open-new-tab-with-delay").click();
    assertThatThrownBy(() -> switchTo().window(1, Duration.ofSeconds(1)))
      .isInstanceOf(NoSuchWindowException.class);
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }
}
