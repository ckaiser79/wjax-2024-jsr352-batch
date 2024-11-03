package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.BadRecordFormatException;
import de.continentale.vu.demo_jsr352.FileReadWriteServiceBean;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceTextFileReaderTest {

  @Mock FileReadWriteServiceBean fileReadWriteServiceBean;

  @InjectMocks private SourceTextFileReader cut;
  private List<PersonTextRecord> persons = new LinkedList<>();

  @Test
  void shouldSetNextCheckpointOnAnyException() throws Exception {

    persons.add(new PersonTextRecord("fn1", "ln", "26", "f"));
    persons.add(new PersonTextRecord(null, "ln", "15", "m"));

    when(fileReadWriteServiceBean.readFile(null)).thenReturn(persons);

    cut.open(null);
    assertThat(cut.checkpointInfo()).isEqualTo(Integer.valueOf(0));

    cut.readItem();
    assertThat(cut.checkpointInfo()).isEqualTo(Integer.valueOf(1));

    assertThatThrownBy(() -> cut.readItem()).isInstanceOf(BadRecordFormatException.class);
    assertThat(cut.checkpointInfo()).isEqualTo(Integer.valueOf(2));

    Object item = cut.readItem();
    assertThat(item).isNull();

    assertThat(cut.checkpointInfo()).isEqualTo(Integer.valueOf(2));
  }
}
