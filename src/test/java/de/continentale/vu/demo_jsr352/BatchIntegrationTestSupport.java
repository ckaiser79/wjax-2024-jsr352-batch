package de.continentale.vu.demo_jsr352;

import org.slf4j.Logger;

import javax.batch.runtime.BatchStatus;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class BatchIntegrationTestSupport {

  private static final Logger logger = getLogger(BatchIntegrationTestSupport.class);

    private static final int MAX_WAITING_COUNTER = 10;

    private static final HttpClient client =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    private final URI batchServerContextUrl;

    public BatchIntegrationTestSupport(final URI batchServerContextUrl) {
        this.batchServerContextUrl = batchServerContextUrl;
    }


    public  BatchStatus waitForBatch(final String executionId)
            throws URISyntaxException, IOException, InterruptedException {

        assertThat(executionId).withFailMessage("missing execution id for waiting to job").isNotEmpty();

        final URI uri = new URI(batchServerContextUrl + "/executions/" + executionId);

        final Set<BatchStatus> finishedStatus =
                EnumSet.of(BatchStatus.COMPLETED, BatchStatus.FAILED, BatchStatus.ABANDONED);

        final HttpRequest request =
                HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(3)).GET().build();

        int counter = 0;
        BatchStatus batchStatus = BatchStatus.STARTED;


        while (!finishedStatus.contains(batchStatus) && counter <= MAX_WAITING_COUNTER) {

            logger.debug("send request: {}", request.toString());
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            final String responseBody = response.body();
            logger.debug("response: statusCode={} body='{}'", response.statusCode(), responseBody);

            final String[] fields = responseBody.split(":");
            batchStatus = BatchStatus.valueOf(fields[fields.length - 2]);

            TimeUnit.SECONDS.sleep(1);
            counter++;
        }

        if (counter > MAX_WAITING_COUNTER) {
            logger.warn("Timeout reached while waiting for batch completion id= {}", executionId);
        }


        return batchStatus;
    }

    public String startBatch(final URI batchStartURL, final Map<String, String> parameters)
            throws IOException, InterruptedException {

        logger.info("Run client for '{}'", batchStartURL);

        final String postBody = createParameterAsJson(parameters);
        logger.info("postBody '{}'", postBody);

        final HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(batchStartURL)
                        .POST(HttpRequest.BodyPublishers.ofString(postBody, UTF_8))
                        .header("Accept-Charset", "UTF-8")
                        .header("Charset", "UTF-8")
                        .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .timeout(Duration.ofSeconds(3))
                        .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final String executionId = response.headers().firstValue("X-Execution-Id").orElse(null);
        logger.info("statusCode={}, ExecutionId = '{}'", response.statusCode(), executionId);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(executionId)
                .withFailMessage("missing execution id on startJob")
                .isNotNull();
        return executionId;
    }

    private static String createParameterAsJson(final Map<String, String> parameters) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
            sb.append("\"")
                    .append(parameter.getKey())
                    .append("\":\"")
                    .append(parameter.getValue())
                    .append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        final String postBody = sb.toString();

        return postBody;
    }
}
