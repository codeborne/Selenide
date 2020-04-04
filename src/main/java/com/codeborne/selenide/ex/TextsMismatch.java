package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

public class TextsMismatch extends UIAssertionError {
  public TextsMismatch(WebElementsCollection collection, List<String> actualTexts,
                       List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
      String.format("Texts mismatch%nActual: " + actualTexts +
        "%nExpected: " + expectedTexts +
        (explanation == null ? "" : "%nBecause: " + explanation) +
        "%nCollection: " + collection.description()));
    super.timeoutMs = timeoutMs;
  }
}
