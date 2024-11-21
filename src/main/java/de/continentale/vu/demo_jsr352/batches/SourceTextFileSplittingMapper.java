package de.continentale.vu.demo_jsr352.batches;

import static java.util.Arrays.setAll;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.IntStream.range;
import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.PacingStopWatch;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import javax.batch.api.BatchProperty;
import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@Named
@Dependent
public class SourceTextFileSplittingMapper implements PartitionMapper {

  private static final Logger logger = getLogger(SourceTextFileSplittingMapper.class);

  private final PacingStopWatch stopWatch = new PacingStopWatch();

  @BatchProperty @Inject private File sourceTextFile;

  @BatchProperty @Inject private Integer maxRecordsPerFile;

  @BatchProperty @Inject private File outputDirectory;

  @BatchProperty @Inject private String outputCharset;

  /**
   * @return number of partions, number of thread, properties of each partition
   */
  @Override
  public PartitionPlan mapPartitions() throws Exception {
    logger.trace(">> mapPartions()");

    outputCharset = requireNonNullElse(outputCharset, "UTF8");
    maxRecordsPerFile = requireNonNullElse(maxRecordsPerFile, 100_000);

    final List<File> files = splitFileEveryXLines(sourceTextFile, maxRecordsPerFile);
    final int numberOfFiles = files.size();

    final Properties[] partitionProperties = new Properties[numberOfFiles];

    setAll(partitionProperties, i -> new Properties());
    range(0, numberOfFiles).forEach(i -> fillProperties(partitionProperties[i], files.get(i)));

    final PartitionPlan plan = createPartitionPlan(partitionProperties);

    logger.trace("<< mapPartions()");
    return plan;
  }

  private PartitionPlan createPartitionPlan(final Properties[] partitionProperties) {

    final PartitionPlan plan = new PartitionPlanImpl();

    plan.setThreads(5);
    plan.setPartitions(partitionProperties.length);
    plan.setPartitionProperties(partitionProperties);

    return plan;
  }

  private static void fillProperties(final Properties partitionProperties, final File file) {
    partitionProperties.setProperty("sourceTextFile", file.getAbsolutePath());
  }

  private List<File> splitFileEveryXLines(final File inputFile, final int maxNumberOfLines) {

    stopWatch.start();

    final List<File> result = new ArrayList<>();

    final String fileNamePattern = createFileNamePattern(inputFile);

    try (final Scanner scanner = new Scanner(new FileReader(inputFile))) {
      int fileIndex = 0;

      while (scanner.hasNextLine()) {

        final String fileName = String.format(fileNamePattern, fileIndex++);
        final File outputFile = new File(outputDirectory, fileName);

        try (final BufferedWriter out =
            new BufferedWriter(new FileWriter(outputFile, Charset.forName(outputCharset), false))) {

          for (int i = 0; i < maxNumberOfLines; i++) {

            final String strLine = scanner.nextLine();
            out.write(strLine);

            if (scanner.hasNextLine()) {
              out.newLine();
            } else {
              break;
            }
          }

        } catch (IOException e) {
          logger.error("unable to write output file " + fileName + ", " + e.getMessage(), e);
        }

        result.add(outputFile);
      }
    } catch (Exception e) {
      logger.error("unable to read input file " + inputFile + ", " + e.getMessage(), e);
    }

    stopWatch.stop();
    logger.trace(
        "-- mapPartions() time-to-split={}, numberOfFiles={}, maxRecordsPerFile={}",
        stopWatch,
        result.size(),
        maxRecordsPerFile);

    return Collections.unmodifiableList(result);
  }

  private static String createFileNamePattern(final File inputFile) {
    final int indexOfExtension = inputFile.getName().lastIndexOf('.');
    if (indexOfExtension < 1) {
      throw new IllegalArgumentException(
          "cannot determine filenames based on '" + inputFile.getName() + "'");
    }

    final String extension = inputFile.getName().substring(indexOfExtension);
    final String prefix = inputFile.getName().substring(0, indexOfExtension);

    final String fileNamePattern = prefix + "-%02d" + extension;
    return fileNamePattern;
  }

  void setSourceTextFile(final File sourceTextFile) {
    this.sourceTextFile = sourceTextFile;
  }

  void setMaxRecordsPerFile(final Integer maxRecordsPerFile) {
    this.maxRecordsPerFile = maxRecordsPerFile;
  }

  void setOutputDirectory(final File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  void setOutputCharset(final String outputCharset) {
    this.outputCharset = outputCharset;
  }
}
