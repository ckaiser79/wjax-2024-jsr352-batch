package de.continentale.vu.demo_jsr352.batches;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

/**
 * Just a waiting batchlet with a configurable timeout.
 *
 * @see <a href="https://www.wildfly.org/news/2021/01/14/batch-processing-stop-job-step/">Howto stop
 *     a Batchlet propertly</a>}
 */
@Named
@Dependent
public class DemoBatchlet implements Batchlet {

  private static final Logger logger = getLogger(DemoBatchlet.class);

  private final AtomicBoolean stopRequested = new AtomicBoolean();

  @Inject @BatchProperty // nicht jobProperty!
  private String jobSourceTextFile;

  @Inject @BatchProperty private String batchletSourceTextFile;

  @Inject @BatchProperty private int maxIterations;

  @Override
  public String process() throws Exception {

    logger.debug("process: maxIterations={}", maxIterations);
    logger.debug("process: jobSourceTextFile={} (from job properties)", jobSourceTextFile);
    logger.debug(
        "process: batchletSourceTextFile={} (from batchlet properties)", batchletSourceTextFile);

    stopRequested.set(false);
    int currentIteration = 0;

    // exit status is a free to choose string to control flow in the steps.
    String exitStatus = "completed-demo-batchlet";

    while (currentIteration < maxIterations) {
      if (shouldStop()) {
        exitStatus = "stopped-my-long-running-batchlet";
        break;
      }

      logger.trace("process: iteration {}/{}", currentIteration, maxIterations);

      Thread.sleep(2000);
      currentIteration++;
    }

    return exitStatus;
  }

  /**
   * @return triggered by external system property
   */
  private boolean shouldStop() {

    final String systemPropertyName = "demo-batchlet-batch.step-start.stopRequested";
    final boolean systemPropertyValue =
        Boolean.parseBoolean(System.getProperty(systemPropertyName));

    if (systemPropertyValue || stopRequested.get()) {
      logger.info(
          "shouldStop: true (property={},stopRequested={})",
          systemPropertyValue,
          stopRequested.get());
    }

    return systemPropertyValue || stopRequested.get();
  }

  @Override
  public void stop() throws Exception {
    stopRequested.set(true);
  }
}
