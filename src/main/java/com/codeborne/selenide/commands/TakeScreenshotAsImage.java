package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.awt.image.BufferedImage;

public class TakeScreenshotAsImage implements Command<BufferedImage> {
  @Override
  public BufferedImage execute(SelenideElement proxy, WebElementSource element, Object[] args) {
    return Screenshots.takeScreenShotAsImage(element.getWebElement());
  }
}
