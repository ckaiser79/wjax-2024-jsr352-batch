package de.continentale.vu.demo_jsr352.listener;

import org.slf4j.Logger;

import javax.batch.api.partition.PartitionCollector;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import static de.continentale.vu.demo_jsr352.listener.Jsr352LoggerUtils.toIdentity;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * There is one instance per partition for each collector.
 */
@Named
@Dependent
public class LoggingPartitionCollector implements PartitionCollector {

  private static final Logger logger = getLogger(LoggingPartitionCollector.class);

  @Inject private StepContext stepContext;

  @Override
  public Serializable collectPartitionData() throws Exception {
    logger.debug(toIdentity("collectPartitionData()", stepContext));

    // send this value to analyser
    return toIdentity("", stepContext);
  }
}
