package de.continentale.vu.demo_jsr352.listener;

import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.BatchUtils;

import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@Named
@Dependent
public class LoggingItemProcessListener implements ItemProcessListener {

  private static final Logger logger = getLogger(LoggingItemProcessListener.class);

  @Inject private StepContext stepContext;

  @Override
  public void beforeProcess(final Object item) throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace("beforeProcess: {} item={}", stepContext.getStepName(), item);
    }
  }

  @Override
  public void afterProcess(final Object item, final Object result) throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace(
              "afterProcess: {}, batchStatus={}, exitStatus={}",
              stepContext.getStepName(),
              stepContext.getBatchStatus(),
              stepContext.getExitStatus());
    }
  }

  @Override
  public void onProcessError(final Object item, final Exception ex) throws Exception {
    logger.error(
            "onProcessError: {}, batchStatus={}, exitStatus={}",
            stepContext.getStepName(),
            stepContext.getBatchStatus(),
            stepContext.getExitStatus());
    BatchUtils.logMetrics(logger, stepContext.getStepName(), stepContext.getMetrics());
  }
}
