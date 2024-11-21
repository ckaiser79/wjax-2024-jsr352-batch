package de.continentale.vu.demo_jsr352.batches;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import javax.batch.api.partition.PartitionPlan;
import org.junit.jupiter.api.Test;

public class SourceTextFileSplittingMapperTest {

  private final SourceTextFileSplittingMapper cut = new SourceTextFileSplittingMapper();

  private static void deleteOutputFiles(final String fileIncludeRegex) {
    final File[] targetFiles = new File("target").listFiles(File::isFile);
    Arrays.stream(targetFiles)
        .filter(file -> file.getName().matches(fileIncludeRegex))
        .forEach(f -> f.delete());
  }

  @Test
  void shouldCreatePlanWithCorrectFileNumber() throws Exception {
    deleteOutputFiles("persons-big.*\\.txt$");

    cut.setSourceTextFile(new File("src/test/resources/persons-big.txt"));
    cut.setOutputDirectory(new File("target"));

    final PartitionPlan plan = cut.mapPartitions();

    assertThat(plan.getPartitionProperties()).hasSize(2);
    assertThat(plan.getPartitions()).isEqualTo(2);
    assertThat(plan.getThreads()).isEqualTo(5);

    final File[] targetFiles = new File("target").listFiles(File::isFile);
    final long createdFiles =
        Arrays.stream(targetFiles)
            .filter(file -> file.getName().matches("persons-big.*\\.txt$"))
            .count();

    assertThat(createdFiles).isEqualTo(2);
  }
}
