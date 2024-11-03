package de.continentale.vu.demo_jsr352.listener;

import org.slf4j.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import static org.slf4j.LoggerFactory.getLogger;

@Named
@Dependent
public class LoggingJobListener implements JobListener {

  private static final Logger logger = getLogger(LoggingJobListener.class);

  private final JobContext jobContext;

  @Inject
  public LoggingJobListener(final JobContext jobContext) {
    this.jobContext = jobContext;
  }

  @Override
  public void beforeJob() throws Exception {

    logger.info("beforeJob {}, id={}", jobContext.getJobName(), jobContext.getExecutionId());

    for (final Object key : jobContext.getProperties().keySet()) {
      final Object value = jobContext.getProperties().get(key);
      logger.debug(
          "beforeJob {}, id={} property: {}={}",
          jobContext.getJobName(),
          jobContext.getExecutionId(),
          key,
          value);
    }
  }

  @Override
  public void afterJob() throws Exception {
    logger.info(
        "afterJob {}, id={}, batchStatus={}, exitStatus={}",
        jobContext.getJobName(),
        jobContext.getExecutionId(),
        jobContext.getBatchStatus(),
        jobContext.getExitStatus());
  }
}
