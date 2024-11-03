package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.PersonRepository;
import de.continentale.vu.demo_jsr352.domain.Person;
import java.io.Serializable;
import java.util.List;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named // TODO is this required?
@Dependent
public class SourceDatabaseReader extends AbstractItemReader {

  @Inject private PersonRepository personRepository;

  private int checkpoint;
  private List<Long> allPrimaryKeys;

  @Override
  public void open(final Serializable checkpoint) throws Exception {

    if (checkpoint != null) {
      this.checkpoint = (int) checkpoint;
    } else {
      this.checkpoint = 0;
    }

    allPrimaryKeys = personRepository.findAllIds(this.checkpoint);
  }

  /**
   * @return null on end-of-data
   */
  @Override
  public Object readItem() throws Exception {

    // abort if everything is read
    final int current = checkpoint;
    if (current >= allPrimaryKeys.size()) {
      return null;
    }
    checkpoint++;


    final long currentId = allPrimaryKeys.get(current);

    // if this returns null, all data is treated as read as well
    final Person result = personRepository.findById(Long.valueOf(currentId));

    return result;
  }

  @Override
  public Serializable checkpointInfo() throws Exception {
    return checkpoint;
  }
}
