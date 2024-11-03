package de.continentale.vu.demo_jsr352.listener;

import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.BatchUtils;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

import java.util.List;

@Named
@Dependent
public class LoggingItemWriteListener implements ItemWriteListener {

  private static final Logger logger = getLogger(LoggingItemWriteListener.class);

  @Inject private StepContext stepContext;

  @Override
  public void beforeWrite(final List<Object> items) throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace("beforeWrite: {} items.size()={}", stepContext.getStepName(), items.size());
    }
  }

  @Override
  public void afterWrite(final List<Object> items) throws Exception {
    if (logger.isTraceEnabled()) {
      logger.trace(
              "afterWrite: {}, batchStatus={}, exitStatus={}",
              stepContext.getStepName(),
              stepContext.getBatchStatus(),
              stepContext.getExitStatus());
    }
  }

  @Override
  public void onWriteError(final List<Object> items, final Exception ex) throws Exception {
    logger.error(
            "onWriteError: {}, batchStatus={}, exitStatus={}",
            stepContext.getStepName(),
            stepContext.getBatchStatus(),
            stepContext.getExitStatus());
    BatchUtils.logMetrics(logger, stepContext.getStepName(), stepContext.getMetrics());
  }
}
