package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DescribeTest {

  @Test
  public void selectorIsReportedAsIs() {
    assertEquals("#firstName", Describe.selector(byCssSelector("#firstName")));
    assertEquals("ExBy.id: firstName", Describe.selector(byId("firstName")));
    assertEquals("ExBy.className: firstName", Describe.selector(byClassName("firstName")));
    assertEquals("ExBy.name: firstName", Describe.selector(byName("firstName")));
  }

  @Test
  public void shortlyForSelenideElementShouldDelegateToOriginalWebElement() {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getTagName()).thenThrow(new StaleElementReferenceException("disappeared"));
    
    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.toWebElement()).thenReturn(webElement);
    doThrow(new ElementShould(null, null, visible, webElement, null)).when(selenideElement).getTagName();
    
    assertEquals("StaleElementReferenceException: disappeared", Describe.shortly(selenideElement));
  }
}
