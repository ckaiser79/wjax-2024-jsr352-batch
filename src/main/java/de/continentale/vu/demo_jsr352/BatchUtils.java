package de.continentale.vu.demo_jsr352;

import org.slf4j.Logger;

import javax.batch.runtime.Metric;

public class BatchUtils {

  public static void logMetrics(final Logger logger, final String prefix, Metric[] metrics) {

    if (logger.isTraceEnabled()) {
      for (Metric m : metrics) {
        logger.info("metrics: {} {}={}", prefix, m.getType(), m.getValue());
      }
    }
  }
}
