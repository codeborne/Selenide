package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotExistingElementTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("elements_disappear_on_click.html");
  }

  @Test
  void shouldNotExist() {
    $("#not_exist").shouldNot(exist);
  }

  @Test
  void shouldNotExistIfParentDoesNotExist() {
    $$("#not_exist").first().$("#multirowTable").shouldNot(exist);
  }

  @Test
  void shouldBeHidden() {
    $("#not_exist").shouldBe(hidden);
  }

  @Test
  void shouldNotBeVisible() {
    $("#not_exist").shouldNotBe(visible);
  }

  @Test
  void shouldNotHaveText_fails_ifElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNotHave(text("Remove me"))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}");
  }

  @Test
  void shouldNotHaveAttribute_fails_ifElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNotHave(attribute("abc"))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}");
  }
}
