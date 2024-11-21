package de.continentale.vu.demo_jsr352.listener;

import static de.continentale.vu.demo_jsr352.listener.Jsr352LoggerUtils.toIdentity;
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

  @Inject
  public LoggingStepListener(final StepContext stepContext) {
    this.stepContext = stepContext;
  }

  @Override
  public void beforeStep() throws Exception {

    logger.debug(toIdentity("beforeStep()", stepContext));

    for (final Object key : stepContext.getProperties().keySet()) {
      final Object value = stepContext.getProperties().get(key);
      logger.trace(
          "beforeStep id={} properties: {}={}", stepContext.getStepExecutionId(), key, value);
    }
  }

  @Override
  public void afterStep() throws Exception {
    logger.debug(
        toIdentity(
            "afterStep() batchStatus: {} exitStatus: {}",
            stepContext,
            stepContext.getBatchStatus(),
            stepContext.getExitStatus()));
  }
}
