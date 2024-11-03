package de.continentale.vu.demo_jsr352.listener;

import static org.slf4j.LoggerFactory.getLogger;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@Named
@Dependent
public class LoggingStepListener implements StepListener {

  private static final Logger logger = getLogger(LoggingStepListener.class);

  private final StepContext stepContext;

  private final String stepExecutionId;

  @Inject
  public LoggingStepListener(final JobContext jobContext, final StepContext stepContext) {
    this.stepContext = stepContext;
    stepExecutionId = jobContext.getExecutionId() + "-" + stepContext.getStepExecutionId();
  }

  @Override
  public void beforeStep() throws Exception {

    logger.info("beforeStep: {}, id={}", stepContext.getStepName(), stepExecutionId);

    for (final Object key : stepContext.getProperties().keySet()) {
      final Object value = stepContext.getProperties().get(key);
      logger.debug("beforeStep id={} properties: {}={}", stepExecutionId, key, value);
    }
  }

  @Override
  public void afterStep() throws Exception {
    logger.info(
        "afterStep: {}, id={}, batchStatus={}, exitStatus={}",
        stepContext.getStepName(),
        stepExecutionId,
        stepContext.getBatchStatus(),
        stepContext.getExitStatus());
  }
}
