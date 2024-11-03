package de.continentale.vu.demo_jsr352.batches;

import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.FileReadWriteServiceBean;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

/** Invoked once per chunk if no exception is thrown. */
@Named
@Dependent
public class TargetTextFileWriter implements ItemWriter {

  private static final Logger logger = getLogger(TargetTextFileWriter.class);

  @Inject private FileReadWriteServiceBean readWriteServiceBean;

  @Inject @BatchProperty private File targetTextFile;

  @Inject private JobContext jobContext;

  private int checkpoint;

  @Override
  public void open(final Serializable checkpoint) throws Exception {

    final File fileNameInjected = targetTextFile;
    final String fileNameJobContext = jobContext.getProperties().getProperty("targetTextFile");

    logger.info("fileNameInjected: {}", fileNameInjected);
    logger.info("fileNameJobContext: {}", fileNameJobContext);

    if (checkpoint != null) {
      this.checkpoint = (int) checkpoint;
    }
  }

  @Override
  public void close() throws Exception {
    // NOOP
  }

  @Override
  public void writeItems(final List<Object> items) throws Exception {

    // List<Object> -> List<Anything>
    final List<PersonTextRecord> records = (List<PersonTextRecord>) (Object) items;

    final int currentCheckpoint = checkpoint;
    checkpoint += items.size();
    readWriteServiceBean.writeFile(targetTextFile, records.iterator(), currentCheckpoint);
  }

  @Override
  public Serializable checkpointInfo() throws Exception {
    return checkpoint;
  }
}
