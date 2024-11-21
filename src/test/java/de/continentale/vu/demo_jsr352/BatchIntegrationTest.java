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

  private static final URI batchServerContextUri;

  static {
    try {
      batchServerContextUri = new URI("http://localhost:8080/wjax-2024-jsr352-batch/rest");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private static final String PERSONS =
      toCompatiblePath(new File("src/test/resources/persons.txt"));

  private static final String PERSONS_BIG =
          toCompatiblePath(new File("src/test/resources/persons-big.txt"));

  private static final String PERSONS_SMALL =
      toCompatiblePath(new File("src/test/resources/persons-small.txt"));

  private static final String PERSONS_WITH_EMPTY_NAMES =
      toCompatiblePath(new File("src/test/resources/persons-small-with-empty-names.txt"));

  private static final String PERSONS_WITH_TIMEOUT =
      toCompatiblePath(new File("src/test/resources/persons-small-with-timeouts.txt"));

  private static URI toURI(final String fileName) {
    try {
      return new URI(batchServerContextUri + "/" + fileName);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static Map<String, String> defaultPersonInputParameter(final String inputFile) {
    // skipped_items_log has default
    return Map.of("source_text_file", inputFile, "item_size", "2000");
  }

  private static Map<String, String> defaultPersonSmallInputParameter() {
    // skipped_items_log has default
    return Map.of("source_text_file", PERSONS_SMALL);
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

  private static Map<String, String> partitionedBatchParameter(final String inputFile, final int maxLinesPerFile) {
    return Map.of(
        "source_text_file",
        inputFile,
        "max_lines_per_file",
        "" + maxLinesPerFile,
        "output_directory",
        toCompatiblePath(new File("target")));
  }

  private final BatchIntegrationTestSupport batchIntegrationTestSupport =
      new BatchIntegrationTestSupport(batchServerContextUri);

  private static String toCompatiblePath(final File file) {
    return file.getAbsolutePath().replace('\\', '/');
  }

  @Test
  void shouldCompleteSimpleChunkBatch() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-batch.xml");
    final Map<String, String> parameters = defaultPersonInputParameter(PERSONS_SMALL);
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  void shouldCompleteDemoOfListeners() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-listener-batch.xml");
    final Map<String, String> parameters = defaultPersonInputParameter(PERSONS_SMALL);
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

    final URI batchStartURL = toURI("demo-file2db-retryable-batch.xml");
    final Map<String, String> parameters = retryableParameter();
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.FAILED);
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
  void shouldCompletePartitionedBatch() throws Exception {

    final URI batchStartURL = toURI("demo-file2db-partitioned-batch.xml");
    final Map<String, String> parameters = partitionedBatchParameter(PERSONS, 1000);
    final String executionId = batchIntegrationTestSupport.startBatch(batchStartURL, parameters);

    final BatchStatus status = batchIntegrationTestSupport.waitForBatch(executionId);
    assertThat(status).isEqualTo(BatchStatus.COMPLETED);
  }
}
