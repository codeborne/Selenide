package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ParametersAreNonnullByDefault
public class Stopwatch {
  private final long endTimeNano;

  public Stopwatch(long timeoutMs) {
    this.endTimeNano = nanoTime() + MILLISECONDS.toNanos(timeoutMs);
  }

  @CheckReturnValue
  public boolean isTimeoutReached() {
    long current = nanoTime();
    System.out.println("Current: " + current + ", end: " + endTimeNano + ", reached: " + (current > endTimeNano));
    return current > endTimeNano;
  }

  public void sleep(long milliseconds) {
    if (isTimeoutReached()) return;

    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

/*
  public static void main(String[] args) {
    System.out.println("ok");
    System.out.println(397227793300L - 397226462400L);
    System.out.println(TimeUnit.NANOSECONDS.toMillis(397227793300L - 397226462400L));

    System.out.println("nok");
    System.out.println(397431381100L < 397431481500L);
    System.out.println(397431381100L - 397431481500L < 0L);
    System.out.println(397431481500L < 397431381100L);
    System.out.println(397431481500L - 397431381100L < 0L);
    System.out.println(TimeUnit.NANOSECONDS.toMillis(397431381100L - 397431481500L));

//    System.out.println(Long.MAX_VALUE);
//    System.out.println(Long.MAX_VALUE - 397431381100L);
  }
*/
}
