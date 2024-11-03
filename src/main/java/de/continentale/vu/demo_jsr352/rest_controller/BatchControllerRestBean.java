package de.continentale.vu.demo_jsr352.rest_controller;

import org.slf4j.Logger;

import static de.continentale.vu.demo_jsr352.rest_controller.JobUtils.toProperties;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@RequestScoped
@Path("/")
public class BatchControllerRestBean {

  private static final Logger logger = getLogger(BatchControllerRestBean.class);

  private Supplier<JobOperator> jobOperatorFactory;

  public BatchControllerRestBean() {
    jobOperatorFactory = BatchRuntime::getJobOperator;
  }

  @GET
  @Path("/{jobXMLName: [^/]+}")
  public Response jobStatus() {
    final Set<BatchStatus> statusFilter =
        EnumSet.of(
            BatchStatus.STARTING,
            BatchStatus.STOPPING,
            BatchStatus.STARTED,
            BatchStatus.FAILED,
            BatchStatus.ABANDONED,
            BatchStatus.COMPLETED);

    final Stream<JobExecution> filteredExecutionsStream = filterExecutions(statusFilter);
    final String body =
        filteredExecutionsStream
            .map(BatchControllerRestBean::executionsAsString)
            .collect(joining("\n"));

    return Response.ok(MediaType.TEXT_PLAIN).entity(body).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{jobXMLName: [^/]+}")
  public Response startJob(
      @PathParam("jobXMLName") String jobXMLName,
      Map<String, String> jobParameters,
      @Context UriInfo uriInfo) {

    final MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
    logger.trace(
        ">> startJob({}, {}, queryParameters={})", jobXMLName, jobParameters, queryParameters);

    final Properties jobParamProperties = toProperties(jobParameters);
    final long executionId = jobOperatorFactory.get().start(jobXMLName, jobParamProperties);

    return Response.ok("executionId=" + executionId).header("X-Execution-ID", executionId).build();
  }

  @GET
  @Path("/")
  public Response index(@Context UriInfo uriInfo) {
    final Set<BatchStatus> statusFilter =
        EnumSet.of(
            BatchStatus.STARTING, BatchStatus.STOPPING, BatchStatus.STARTED, BatchStatus.FAILED);

    final Stream<JobExecution> filteredExecutionsStream = filterExecutions(statusFilter);

    final String body =
        filteredExecutionsStream
            .map(BatchControllerRestBean::executionsAsString)
            .collect(joining("\n"));

    return Response.ok(MediaType.TEXT_PLAIN).entity(body).build();
  }

  private static String executionsAsString(JobExecution jobExecution) {
    return jobExecution.getExecutionId()
        + ":"
        + jobExecution.getJobName()
        + ":"
        + jobExecution.getLastUpdatedTime()
        + ":"
        + jobExecution.getBatchStatus()
        + ":"
        + jobExecution.getExitStatus();
  }

  private Stream<JobExecution> filterExecutions(final Set<BatchStatus> statusFilter) {
    return jobOperatorFactory.get().getJobNames().stream()
        .flatMap(jobName -> filterInstancesByName(jobName))
        .flatMap(jobInstance -> filterJobExecutions(jobInstance, statusFilter))
        .sorted((a, b) -> b.getLastUpdatedTime().compareTo(a.getLastUpdatedTime()));
  }

  private Stream<JobExecution> filterJobExecutions(
      final JobInstance jobInstance, final Set<BatchStatus> statusFilter) {
    return jobOperatorFactory.get().getJobExecutions(jobInstance).stream()
        .filter(e -> statusFilter.contains(e.getBatchStatus()));
  }

  private Stream<JobInstance> filterInstancesByName(final String jobName) {
    return jobOperatorFactory
        .get()
        .getJobInstances(jobName, 0, jobOperatorFactory.get().getJobInstanceCount(jobName))
        .stream();
  }


  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/executions/{executionId: \\d+}")
  public Response queryJobExecution(
          @PathParam("executionId") long executionId) {
    JobExecution jobExecution = jobOperatorFactory.get().getJobExecution(executionId);

    final String bodyResult = executionsAsString(jobExecution);
    return Response.ok(bodyResult, MediaType.TEXT_PLAIN_TYPE)
            .build();
  }

}
