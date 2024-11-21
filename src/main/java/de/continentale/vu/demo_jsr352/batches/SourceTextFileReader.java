package de.continentale.vu.demo_jsr352.batches;

import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.BadRecordFormatException;
import de.continentale.vu.demo_jsr352.FileReadWriteServiceBean;
import de.continentale.vu.demo_jsr352.TimeoutException;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@Named
@Dependent
public class SourceTextFileReader extends AbstractItemReader {

  private static final Logger logger = getLogger(SourceTextFileReader.class);

  private final FileReadWriteServiceBean fileReadWriteService;

  @Inject @BatchProperty private File sourceTextFile;

  private int checkpoint = 0;
  private List<PersonTextRecord> personTextRecords;

  @Inject
  public SourceTextFileReader(final FileReadWriteServiceBean fileReadWriteService) {
    this.fileReadWriteService = fileReadWriteService;
  }

  @Override
  public void open(final Serializable checkpoint) throws Exception {
    this.checkpoint = checkpoint == null ? 0 : (Integer) checkpoint;

    personTextRecords = fileReadWriteService.readFile(sourceTextFile);
  }

  @Override
  public Object readItem() throws Exception {

    // abort if all is read
    if (checkpoint >= personTextRecords.size()) {
      return null;
    }

    final PersonTextRecord record = personTextRecords.get(checkpoint);
    checkpoint++; // directly after reading the data and before any exception; endless loop in
                  // listener!

    assertValidRecord(record);
    assertNoTimeout(record);

    logger.trace("readItem: {} at {}", record, checkpoint);
    return record;
  }

  private void assertNoTimeout(final PersonTextRecord record) {
    if ("throw-timeout".equals(record.getFirstName())) {
      throw new TimeoutException(record + ", checkpoint=" + checkpoint);
    }
  }

  private void assertValidRecord(final PersonTextRecord record) throws BadRecordFormatException {
    if (record.getFirstName() == null || record.getFirstName().isEmpty()) {
      throw new BadRecordFormatException("firstName is null or empty", record);
    }
    if (record.getLastName() == null || record.getLastName().isEmpty()) {
      throw new BadRecordFormatException("lastName is null or empty", record);
    }
  }

  @Override
  public Serializable checkpointInfo() throws Exception {
    return checkpoint;
  }
}
