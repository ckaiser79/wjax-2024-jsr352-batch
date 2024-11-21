package de.continentale.vu.demo_jsr352.listener;

import de.continentale.vu.demo_jsr352.BatchUtils;
import org.slf4j.Logger;

import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.runtime.Metric;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Reader;

import static org.slf4j.LoggerFactory.getLogger;

@Named
@Dependent
public class LoggingItemReadListener implements ItemReadListener {

  private static final Logger logger = getLogger(LoggingItemReadListener.class);

  @Inject private StepContext stepContext;

  private int counter = 0;

  @Override
  public void beforeRead() throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace("beforeRead: {} counter={}", stepContext.getStepName(), counter);
    }
  }

  @Override
  public void afterRead(final Object item) throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace(
          "afterRead: {}, batchStatus={}, exitStatus={}",
          stepContext.getStepName(),
          stepContext.getBatchStatus(),
          stepContext.getExitStatus());
    }
  }

  @Override
  public void onReadError(final Exception ex) throws Exception {

    logger.error(
        "onReadError: {}, batchStatus={}, exitStatus={}",
        stepContext.getStepName(),
        stepContext.getBatchStatus(),
        stepContext.getExitStatus());
    BatchUtils.logMetrics(logger, stepContext.getStepName(), stepContext.getMetrics());
  }
}
