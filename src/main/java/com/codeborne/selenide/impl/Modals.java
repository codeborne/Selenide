package com.codeborne.selenide.impl;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.switchTo;

public class Modals {

  public String confirm() {
    return confirm(null);
  }

  public String confirm(String expectedDialogText) {
    Alert alert = switchTo().alert();
    String actualDialogText = alert.getText();
    alert.accept();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String prompt() {
    return prompt(null, null);
  }

  public String prompt(String inputText) {
    return prompt(null, inputText);
  }

  public String prompt(String expectedDialogText, String inputText) {
    Alert alert = switchTo().alert();
    String actualDialogText = alert.getText();
    if (inputText != null)
      alert.sendKeys(inputText);
    alert.accept();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String dismiss() {
    return dismiss(null);
  }

  public String dismiss(String expectedDialogText) {
    Alert alert = switchTo().alert();
    String actualDialogText = alert.getText();
    alert.dismiss();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  private static void checkDialogText(String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      Screenshots.takeScreenShot(Selenide.class.getName(), Thread.currentThread().getName());
      throw new DialogTextMismatch(actualDialogText, expectedDialogText);
    }
  }
}
