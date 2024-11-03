package de.continentale.vu.demo_jsr352.listener;

import de.continentale.vu.demo_jsr352.TimeoutException;
import org.slf4j.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@Named
@Dependent
public class WaitingRetryReadListener implements RetryReadListener {

  private static final Logger logger = getLogger(WaitingRetryReadListener.class);

  @Inject
  @BatchProperty
  private long waitInSeconds;

  @Override
  public void onRetryReadException(final Exception ex) throws Exception {
    if(ex instanceof TimeoutException) {
      logger.error("onRetryReadException got {}, delay by {} seconds", ex.getMessage(), waitInSeconds);
      TimeUnit.SECONDS.sleep(waitInSeconds);
    }

  }

  public void setWaitInSeconds(final long waitInSeconds) {
    this.waitInSeconds = waitInSeconds;
  }
}
