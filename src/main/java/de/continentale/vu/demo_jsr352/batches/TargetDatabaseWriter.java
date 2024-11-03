package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.PersonRepository;
import de.continentale.vu.demo_jsr352.domain.Person;

import java.io.Serializable;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Dependent
public class TargetDatabaseWriter extends AbstractItemWriter {

  private int checkpoint = 0;

  @Inject private PersonRepository personRepository;

  @Override
  public void open(final Serializable checkpoint) throws Exception {}

  @Override
  public Serializable checkpointInfo() throws Exception {
    return checkpoint;
  }

  @Override
  public void writeItems(final List<Object> items) throws Exception {

    final int startIndex = checkpoint;

    for (int i = startIndex; i < items.size(); i++) {

      final Person person = (Person) items.get(i);

      // TODO handle JPA Exceptions!
      checkpoint++;
      personRepository.save(person);

    }
  }
}
