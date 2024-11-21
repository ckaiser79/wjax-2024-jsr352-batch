package de.continentale.vu.demo_jsr352.listener;

import org.slf4j.Logger;

import javax.batch.api.partition.PartitionReducer;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import static de.continentale.vu.demo_jsr352.listener.Jsr352LoggerUtils.toIdentity;
import static org.slf4j.LoggerFactory.getLogger;

@Named
@Dependent
public class LoggingPartitionReducer implements PartitionReducer {

  private static final Logger logger = getLogger(LoggingPartitionReducer.class);

  @Inject private StepContext stepContext;

  @Override
  public void beginPartitionedStep() throws Exception {
    logger.debug(toIdentity("reducer-beginPartitionedStep()", stepContext));
  }

  @Override
  public void beforePartitionedStepCompletion() throws Exception {
    logger.debug(toIdentity("reducer-beforePartitionedStepCompletion()", stepContext));
  }

  @Override
  public void rollbackPartitionedStep() throws Exception {
    logger.debug(toIdentity("reducer-rollbackPartitionedStep()", stepContext));
  }

  @Override
  public void afterPartitionedStepCompletion(final PartitionStatus status) throws Exception {
    logger.debug(toIdentity("reducer-afterPartitionedStepCompletion()", stepContext));
  }
}
