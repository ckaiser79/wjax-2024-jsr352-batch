package de.continentale.vu.demo_jsr352;

import de.continentale.vu.demo_jsr352.batches.PersonTextRecordToStringFunction;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class FileReadWriteServiceBean {

  private static final Logger logger = getLogger(FileReadWriteServiceBean.class);

  private static final String TAB = "\t";

  public List<PersonTextRecord> readFile(final File inputTextFile) throws IOException {

    final List<PersonTextRecord> personTextRecords = new LinkedList<>();

    try (final BufferedReader reader = new BufferedReader(new FileReader(inputTextFile))) {

      String line;
      int lineNumber = 0;

      while ((line = reader.readLine()) != null) {

        lineNumber++;
        final String[] fields = line.split(TAB);

        final PersonTextRecord record = PersonTextRecord.valueOf(lineNumber, fields);

        logger.trace("readFile(File) record {}", record);
        personTextRecords.add(record);
      }
    }

    logger.debug("readFile({}) #personTextRecords {}", inputTextFile, personTextRecords.size());
    return personTextRecords;
  }

  /**
   * open a file for appending and write all records in it.
   *
   * @return number of records written
   */
  public int writeFile(
      final File outputTextFile, Iterator<PersonTextRecord> records, int skipLines) {

    int linesWritten = 0;
    final PersonTextRecordToStringFunction personTextRecordToStringFunction =
        new PersonTextRecordToStringFunction();

    // open file for appending (in case batch is restarted)
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputTextFile, true))) {

      while (records.hasNext()) {

        // goto record, which should be written
        if (linesWritten < skipLines) {
          continue;
        }

        final PersonTextRecord record = records.next();
        final String line = personTextRecordToStringFunction.apply(record);

        logger.trace("writeFile(File,Iterator,int) line '{}'", line);
        writer.append(line);
        writer.flush(); // save after each line, TODO performance improvement?

        linesWritten++;
      }

    } catch (IOException e) {
      logger.error("file was not writte completly: {} - {}", outputTextFile, e.getMessage(), e);
    }

    logger.debug(
        "writeFile({},records,{}) #linesWritten {}", outputTextFile, skipLines, linesWritten);
    return linesWritten;
  }
}
