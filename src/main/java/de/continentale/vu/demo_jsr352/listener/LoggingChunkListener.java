package de.continentale.vu.demo_jsr352.listener;

import static de.continentale.vu.demo_jsr352.listener.Jsr352LoggerUtils.toIdentity;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.runtime.Metric;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@Named
@Dependent
public class LoggingChunkListener implements ChunkListener {

  private static final Logger logger = getLogger(LoggingChunkListener.class);

  private final StepContext stepContext;

  private final String chunkExecutionId;

  @Inject
  public LoggingChunkListener(final JobContext jobContext, final StepContext stepContext) {
    this.stepContext = stepContext;
    this.chunkExecutionId = jobContext.getExecutionId() + "-" + stepContext.getStepExecutionId();
  }

  @Override
  public void beforeChunk() throws Exception {
    logger.debug(toIdentity("beforeChunk()", stepContext));
  }

  @Override
  public void onError(final Exception ex) throws Exception {
    logger.warn(toIdentity("onError({})", stepContext, ex.getMessage()));
  }

  @Override
  public void afterChunk() throws Exception {
    logger.debug(toIdentity("afterChunk()", stepContext));

    final Metric readCount =
        Arrays.stream(stepContext.getMetrics())
            .filter(m -> m.getType().equals(Metric.MetricType.READ_COUNT))
            .findFirst()
            .orElse(null);
    final Metric writeCount =
        Arrays.stream(stepContext.getMetrics())
            .filter(m -> m.getType().equals(Metric.MetricType.WRITE_COUNT))
            .findFirst()
            .orElse(null);

    logger.info("afterChunk: {}, {}, threadId: {}", readCount, writeCount, Thread.currentThread().getId());
  }
}
