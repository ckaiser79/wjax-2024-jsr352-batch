package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.domain.Person;
import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class TextFileToDatabaseProcessor implements ItemProcessor {

  private static final GenderMappingTextToDatabaseFunction GENDER_MAPPER_TEXT2DB =
      new GenderMappingTextToDatabaseFunction();

  /**
   * @return null to skip item
   */
  @Override
  public Object processItem(final Object item) throws Exception {

    final PersonTextRecord textRecord = (PersonTextRecord) item;

    final Person person = new Person();
    person.setFirstName(textRecord.getFirstName());
    person.setLastName(textRecord.getLastName());
    person.setGender(GENDER_MAPPER_TEXT2DB.apply(textRecord.getGender()));

    return person;
  }
}
