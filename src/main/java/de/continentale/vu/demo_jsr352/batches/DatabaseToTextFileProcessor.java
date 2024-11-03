package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.domain.Gender;
import de.continentale.vu.demo_jsr352.domain.Person;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;

import javax.batch.api.chunk.ItemProcessor;

public class DatabaseToTextFileProcessor implements ItemProcessor {

  /**
   * Only include those person, where we new the gender
   * @return null to skip item
   */
  @Override
  public Object processItem(final Object item) throws Exception {
    final Person person = (Person) item;

    if(person.getGender() == null || Gender.UNKNOWN.equals(person.getGender())) {
      return null;
    }

    final PersonTextRecord textRecord =
        new PersonTextRecord(
            person.getFirstName(), person.getLastName(), "-1", person.getGender().toString());
    return textRecord;
  }
}
