package de.continentale.vu.demo_jsr352.listener;

import org.slf4j.Logger;

import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import static de.continentale.vu.demo_jsr352.listener.Jsr352LoggerUtils.toIdentity;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * There is one instance per Step of each Analyzer
 */
@Named
@Dependent
public class LoggingPartitionAnalyzer implements PartitionAnalyzer {

  private static final Logger logger = getLogger(LoggingPartitionAnalyzer.class);

  @Inject
  private StepContext stepContext;

  @Override
  public void analyzeCollectorData(final Serializable data) throws Exception {
    logger.debug(toIdentity("analyzeCollectorData({})", stepContext, data));
  }

  @Override
  public void analyzeStatus(final BatchStatus batchStatus, final String exitStatus)
      throws Exception {
    logger.debug(toIdentity("analyzeStatus({}, {})", stepContext, batchStatus, exitStatus));
  }
}
