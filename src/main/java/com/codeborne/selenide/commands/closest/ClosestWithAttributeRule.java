package com.codeborne.selenide.commands.closest;

import static java.lang.String.format;

public class ClosestWithAttributeRule extends SelectorValidation implements ClosestRule {

  private String xpath;

  @Override
  public boolean evaluate(String selector) {
    boolean evaluationResult = false;
    if (isAttribute(selector) && !containsAttributeValue(selector)) {
      String attribute = selector.substring(1, selector.length() - 1);
      this.xpath = format("ancestor::*[@%s][1]", attribute);
      evaluationResult = true;
    }
    return evaluationResult;
  }

  @Override
  public ClosestResult getClosestResult() {
    return new ClosestResult(xpath);
  }
}
