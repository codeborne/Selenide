package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import java.io.File;

public class TakeScreenshot implements Command<File> {
  @Override
  @Nullable
  public File execute(SelenideElement proxy, WebElementSource element, Object[] args) {
    return ScreenShotLaboratory.getInstance().takeScreenshot(element.driver(), element.getWebElement());
  }
}
