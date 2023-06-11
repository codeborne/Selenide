package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.Condition;

public class DateFormatConditionOptions implements DateConditionOptions {

  private final LocalDateFormatCondition formatter;

  DateFormatConditionOptions(LocalDateFormatCondition formatter) {
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return formatter;
  }
}
