package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.ClickOptions.withOffset;
import static com.codeborne.selenide.Selenide.$;
import static integration.Coordinates.coordinates;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Click with offset - calculates offset from the center of clicked element.
 * Element '#page' is 800x600 -> its center is 400x300.
 * Click to (400+123, 300+222) -> (523, 522)
 */
final class ClickRelativeTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_relative_click_position.html");
  }

  @Test
  void userCanClickElementWithOffsetPosition_withActions() {
    Configuration.clickViaJs = false;

    $("#page")
      .click(usingDefaultMethod())
      .click(usingDefaultMethod().offset(10, 10))
      .click(usingDefaultMethod().offset(123, 222));

    verifyCoordinates(523, 522);
  }

  @Test
  void userCanClickElementWithOffsetPosition_withJavascript() {
    Configuration.clickViaJs = true;

    $("#page").click(usingDefaultMethod().offset(133, 222));

    verifyCoordinates(533, 522);
  }

  @Test
  void userCanClickElementWithOffset() {
    $("#page").click(withOffset(123, 222));

    verifyCoordinates(523, 522);
  }

  @Test
  void userCanClickElementWithOffsetPositions_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offset(123, 222));

    verifyCoordinates(523, 522);
  }

  @Test
  void userCanClickElementWithOffsetXPosition_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offsetX(123));

    verifyCoordinates(523, 300);
  }

  @Test
  void screenshotIsTaken_ifClickWithOffset_getsOutsideOfElement() {
    Configuration.timeout = 123;

    assertThatThrownBy(() -> $("#page").click(usingDefaultMethod().offsetX(9999999)))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContaining("MoveTargetOutOfBoundsException")
      .hasMessageContaining("out of bounds")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 123 ms.")
      .hasCauseInstanceOf(MoveTargetOutOfBoundsException.class);
  }

  private void verifyCoordinates(int expectedX, int expectedY) {
    $("#coords").shouldHave(coordinates(expectedX, expectedY));
  }
}
