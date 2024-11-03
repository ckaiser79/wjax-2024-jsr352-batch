package de.continentale.vu.demo_jsr352;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;
import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

class FileReadWriteServiceBeanTest {

  private FileReadWriteServiceBean bean = new FileReadWriteServiceBean();

  @Test
  void shouldReadLinesWithEmptyFirstName() throws Exception {
    final File inputTextFile = new File("src/test/resources/persons-small-with-empty-names-unittest.txt");
    final List<PersonTextRecord> records = bean.readFile(inputTextFile);

    assertThat(records).hasSize(2);
    assertThat(records.get(1).getFirstName()).isEmpty();
    assertThat(records.get(0).getLastName()).isEmpty();
  }

  @Test
  void playWithBean() throws Exception {

    final File inputTextFile = new File("src/test/resources/persons.txt");
    final List<PersonTextRecord> records = bean.readFile(inputTextFile);

    final File outputTextFile = new File("target/out.txt");
    bean.writeFile(outputTextFile, records.iterator(), 0);

    assertEquals(5000, records.size());
    FileUtils.contentEqualsIgnoreEOL(inputTextFile, outputTextFile, "UTF-8");
  }
}
