package de.continentale.vu.demo_jsr352.listener;

import de.continentale.vu.demo_jsr352.BadRecordFormatException;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import static org.slf4j.LoggerFactory.getLogger;

@Named
@Dependent
public class DemoBadRecordLoggingListener implements SkipReadListener {

  private static final Logger logger = getLogger(DemoBadRecordLoggingListener.class);

  @Inject private JobContext jobContext;

  @Override
  public void onSkipReadItem(final Exception ex) throws Exception {

    if (ex instanceof BadRecordFormatException) {

      final BadRecordFormatException ee = (BadRecordFormatException) ex;

      final File fileName = new File(jobContext.getProperties().getProperty("skippedItemsLog"));

      logger.debug("onSkipReadItem: save skipped item '{}' to {}", ee.getRecord(), fileName);

      try(FileWriter w = new FileWriter(fileName, true)) {
        final BufferedWriter writer = new BufferedWriter(w);

        writer.write(ee.getMessage());
        writer.write(ee.getRecord().toString());
        writer.newLine();

        writer.flush();
      }

      logger.trace("onSkipReadItem: closed {}", ee.getRecord(), fileName);
    }
  }
}
