package com.codeborne.selenide;

import com.codeborne.selenide.ex.ConditionNotMatchException;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;

/**
 * Class provide conditional polling wait by predicate with ignore exceptions.
 */

public class ConditionWaiter {

  private static final Logger logger = LoggerFactory.getLogger(ConditionWaiter.class);

  public static void waitFor(Conditional conditional, Predicate predicate, String message) {
    waitFor(conditional, predicate, message, Duration.ofMillis(10000));
  }

  public static void waitFor(Conditional conditional, Predicate predicate, String message, Duration timeout) {
    waitFor(conditional, predicate, message, timeout, Duration.ofMillis(500));
  }

  public static void waitFor(Conditional conditional, Predicate predicate, String message, Duration timeout, Duration polling) {
    try {
      await()
        .pollInterval(polling)
        .timeout(timeout)
        .pollInSameThread()
        .until(() -> checkUnThrowable(conditional, predicate));
    } catch (ConditionTimeoutException e) {
      throw new ConditionNotMatchException(message);
    }
  }

  private static boolean checkUnThrowable(Conditional conditional, Predicate<Conditional> predicate) {
    try {
      return predicate.test(conditional);
    } catch (Exception e) {
      logger.info("Fail to check condition", e);
    }
    return false;
  }
}
