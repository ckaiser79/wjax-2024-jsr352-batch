package de.continentale.vu.demo_jsr352;

import java.time.Duration;
import java.util.TreeMap;

public class PacingStopWatch {

  /**
   * a pace is a duration taken at a special event.
   */
  private TreeMap<String, Duration> paces = new TreeMap<>();

  private long startTime;
  private long endTime;
  private boolean running;

  public void start() {
    if (running) {
      return;
    }

    this.startTime = System.currentTimeMillis();
    this.running = true;
  }

  public Duration stop() {
    if (!running) {
      return Duration.ZERO;
    }

    this.endTime = System.currentTimeMillis();
    this.running = false;

    return getElapsedTime();
  }

  public void reset() {
    this.running = false;
    this.startTime = 0;
    this.endTime = 0;
  }

  public Duration getElapsedTime() {
    final Duration result;

    if (running) {
      result = Duration.ofMillis(System.currentTimeMillis() - startTime);
    } else {
      result = Duration.ofMillis(endTime - startTime);
    }

    return result;
  }

  public Duration pace(final String label) {
    final Duration snapshotTime = getElapsedTime();
    paces.put(label, snapshotTime);
    return snapshotTime;
  }

  public TreeMap<String, Duration> getPaces() {
    return paces;
  }

  @Override
  public String toString() {
    return getElapsedTime().toString();
  }
}
