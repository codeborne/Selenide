package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;
import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class Ancestor implements Command<SelenideElement> {
  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String tagOrClass = firstOf(args);
    int indexPredicate = args.length > 1 ?
      (args[1] instanceof Integer ? (int) args[1] + 1 : 1) :
      1;

    String xpath = tagOrClass.startsWith(".") ?
      format("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][" + indexPredicate + "]",
        tagOrClass.substring(1)) :
      format("ancestor::%s[" + indexPredicate + "]",
        tagOrClass);
    return locator.find(proxy, By.xpath(xpath), 0);
  }
}
