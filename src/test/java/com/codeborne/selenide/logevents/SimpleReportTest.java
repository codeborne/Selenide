package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.assertj.core.api.Assertions.assertThat;

final class SimpleReportTest {
  private static final Pattern REGEX_LINE_SEPARATOR = Pattern.compile("\\n");

  @Test
  void reportShouldNotThrowNpe() {
    new SimpleReport().finish("test");
  }

  @Test
  void reportSample() throws IOException {
    String report = new SimpleReport().generateReport("userCanLogin", asList(
      new Log("webdriver", "create()", PASS, millisToNanos(100), millisToNanos(200)),
      new Log("open", "google.com", PASS, millisToNanos(200), millisToNanos(300)),
      new Log("#loginButton", "click(200, 100)", PASS, millisToNanos(300), millisToNanos(400)),
      new Log(".user-name", "should have(text Степан)", FAIL, millisToNanos(400), millisToNanos(500))
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test1.txt"));
  }

  @Test
  void reportWithTooLongValue() throws IOException {
    String report = new SimpleReport().generateReport("userCanOpenVeryLongUrl", asList(
      new Log("webdriver", "create()", PASS, 0, millisToNanos(100)),
      new Log("open", "https://some.com/authorization/authentication/modularization/internationalization", PASS,
        millisToNanos(100), millisToNanos(200)),
      new Log("#loginButton", "click", FAIL, millisToNanos(200), millisToNanos(300))
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test2.txt"));
  }

  @Test
  void reportWithLongSelectors() throws IOException {
    String report = new SimpleReport().generateReport("userCanUseVeryLongSelectors", asList(
      new Log("open", "about:blank", PASS, 0, millisToNanos(100)),
      new Log("#any-long-ugly-selector-should-be-entirely-visible", "click", PASS, millisToNanos(100), millisToNanos(200)),
      new Log("close", "", FAIL, millisToNanos(200), millisToNanos(300))
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test4.txt"));
  }

  @Test
  void reportWithNestingEvents() throws IOException {
    String report = new SimpleReport().generateReport("nesting steps", asList(
      new Log("login", "", PASS, 0, millisToNanos(1000)),
      new Log("level 1", "click", PASS, millisToNanos(100), millisToNanos(800)),
      new Log("level 2", "click", PASS, millisToNanos(200), millisToNanos(300)),
      new Log("level 2", "click", PASS, millisToNanos(300), millisToNanos(400)),
      new Log("level 2", "click", PASS, millisToNanos(400), millisToNanos(600)),
      new Log("level 3 with long text field that should not break the report", "click", PASS, millisToNanos(400), millisToNanos(500)),
      new Log("logout", "", FAIL, millisToNanos(1000), millisToNanos(1200)),
      new Log("level 1", "click", PASS, millisToNanos(1000), millisToNanos(1100)),
      new Log("level 1", "click", FAIL, millisToNanos(1100), millisToNanos(1200))
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test5.txt"));
  }

  @Test
  void emptyReport() throws IOException {
    String report = new SimpleReport().generateReport("userMightNotHaveDoneAnySteps", emptyList());

    assertThat(report).isEqualTo(sample("/simple-report-test3.txt"));
  }

  private String sample(String filename) throws IOException {
    return REGEX_LINE_SEPARATOR.matcher(resourceToString(filename, UTF_8)).replaceAll(lineSeparator());
  }

  private long millisToNanos(long millis) {
    return MILLISECONDS.toNanos(millis);
  }

  private static class Log extends SelenideLog {
    private final long startTime;
    private final long endTime;

    Log(String element, String subject, EventStatus status, long start, long end) {
      super(element, subject);
      setStatus(status);
      startTime = start;
      endTime = end;

    }

    @Override
    public long getStartTime() {
      return startTime;
    }

    @Override
    public long getEndTime() {
      return endTime;
    }
  }
}
