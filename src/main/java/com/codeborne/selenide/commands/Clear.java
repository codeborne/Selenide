package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Clean the input field value.
 */
@ParametersAreNonnullByDefault
public class Clear implements Command<SelenideElement> {
  @Nonnull
  @CheckReturnValue
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement input = locator.findAndAssertElementIsEditable();
    clearAndTrigger(locator.driver(), input);
    return proxy;
  }

  /**
   * Clear the input content and trigger "change" and "blur" events
   *
   * <p>
   * This is the shortest keys combination I found in May 2022.<br>
   * It seems to work in Firefox, Chrome on Mac and on Linux smoothly.
   * </p>
   */
  protected void clearAndTrigger(Driver driver, WebElement input) {
    clear(driver, input);
    driver.executeJavaScript("document.activeElement?.blur()");
  }

  /**
   * Clear the input content without triggering "change" and "blur" events
   */
  public void clear(Driver driver, WebElement input) {
    input.clear();
  }
}
