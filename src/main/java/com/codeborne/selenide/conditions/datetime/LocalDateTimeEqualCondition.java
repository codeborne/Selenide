package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class LocalDateTimeEqualCondition extends Condition {

  private final LocalDateTime expectedDateTime;
  private final LocalDateTimeFormatCondition formatCondition;

  public LocalDateTimeEqualCondition(LocalDateTime expectedDateTime, String pattern) {
    this(expectedDateTime, new LocalDateTimeFormatCondition(pattern));
  }

  LocalDateTimeEqualCondition(LocalDateTime expectedDateTime, LocalDateTimeFormatCondition formatCondition) {
    super("datetime value");
    this.expectedDateTime = expectedDateTime;
    this.formatCondition = formatCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    if (formatted.verdict() == REJECT) return formatted;

    LocalDateTime localDateTimeValue = (LocalDateTime) formatted.actualValue();

    if (localDateTimeValue == null) throw new IllegalStateException("Format condition returns null, not datetime");

    return new CheckResult(expectedDateTime.isEqual(localDateTimeValue), formatCondition.format(localDateTimeValue));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s: \"%s\" (with %s)", getName(), formatCondition.format(expectedDateTime), formatCondition);
  }
}
