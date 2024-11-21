package de.continentale.vu.demo_jsr352;

import org.slf4j.Logger;

import javax.batch.runtime.Metric;
import javax.batch.runtime.Metric.MetricType;
import java.util.Arrays;
import java.util.Set;

public class BatchUtils {

  public static void logMetrics(final Logger logger, final String prefix, Metric[] metrics) {

    if (logger.isTraceEnabled()) {
      final Set<MetricType> includedMetrics =
          Set.of(
              MetricType.READ_COUNT,
              MetricType.WRITE_COUNT,
              MetricType.COMMIT_COUNT,
              MetricType.ROLLBACK_COUNT);
      Arrays.stream(metrics)
          .filter(m -> includedMetrics.contains(m.getType()))
          .forEach(m -> logger.trace("metrics: {} {}", prefix, m));
    }
  }
}
