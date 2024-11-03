package de.continentale.vu.demo_jsr352;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.of;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * This class connects to a local applicationserver (see <code>batchServerContextUrl</code>) and
 * start each job in directory <code>batch-jobs</code>.
 *
 * <p>It waits up to 10 seconds for a finished batch status, then start the next batch.
 */
public class BatchIntegrationTest {

  private static final Logger logger = getLogger(BatchIntegrationTest.class);

  private static final URI batchServerContextUri;

  static {
    try {
      batchServerContextUri = new URI("http://localhost:8080/wjax-2024-jsr352-batch/rest");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private static final String PERSONS =
      new File("src/test/resources/persons.txt").getAbsolutePath().replace('\\', '/');

  private static final String PERSONS_WITH_EMPTY_NAMES =
      new File("src/test/resources/persons-small-with-empty-names.txt")
          .getAbsolutePath()
          .replace('\\', '/');

  private static final String PERSONS_WITH_TIMEOUT =
      new File("src/test/resources/persons-small-with-timeouts.txt")
          .getAbsolutePath()
          .replace('\\', '/');

  private static Map<String, String> defaultPersonInputParameter() {
    // skipped_items_log has default
    return Map.of("source_text_file", PERSONS, "item_size", "2000");
  }

  private static Map<String, String> batchletParameter(final int maxIterations) {
    return Map.of("source_text_file", PERSONS, "max_iterations", String.valueOf(maxIterations));
  }

  private static Map<String, String> skippableParameter() {
    // skipped_items_log has default
    return Map.of("source_text_file", PERSONS_WITH_EMPTY_NAMES);
  }

  private static Map<String, String> retryableParameter() {
    // skipped_items_log has default
    return Map.of("source_text_file", PERSONS_WITH_TIMEOUT);
  }

  private final BatchIntegrationTestSupport batchIntegrationTestSupport =
      new BatchIntegrationTestSupport(batchServerContextUri);

  @Test
  void shouldCompleteSimpleChunkBatch() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-batch.xml");
    final Map<String, String> parameters = defaultPersonInputParameter();
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  void shouldCompleteBatchlet() throws Exception {

    final URI batchStartURL = toURI("demo-batchlet-batch.xml");
    final Map<String, String> parameters = batchletParameter(2);
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  void shouldCompleteDemoOfListeners() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-listener-batch.xml");
    final Map<String, String> parameters = defaultPersonInputParameter();
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  void shouldCompleteBatchWithSkippedRecord() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-batch.xml");
    final Map<String, String> parameters = skippableParameter();
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  void shouldFailIfRetriesFail() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-listener-batch.xml");
    final Map<String, String> parameters = retryableParameter();
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.FAILED);
  }

  private static URI toURI(final String fileName) {
    try {
      return new URI(batchServerContextUri + "/" + fileName);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
