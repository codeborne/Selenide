package com.codeborne.selenide;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @since 5.23.0
 */
public class SessionStorageConditions {

  public static Predicate<SessionStorage> item(String item) {
    return localStorage -> localStorage.containsItem(item);
  }

  public static Predicate<SessionStorage> itemWithValue(String item, String value) {
    return localStorage -> Objects.equals(localStorage.getItem(item), value);
  }
}
