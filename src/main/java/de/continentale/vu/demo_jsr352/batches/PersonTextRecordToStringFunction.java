package de.continentale.vu.demo_jsr352.batches;

import de.continentale.vu.demo_jsr352.vo.PersonTextRecord;

import java.util.function.Function;

public class PersonTextRecordToStringFunction implements Function<PersonTextRecord, String> {

  private static final String TAB = "\t";

  @Override
  public String apply(final PersonTextRecord personTextRecord) {
    final StringBuilder sb = new StringBuilder();
    sb.append(personTextRecord.getFirstName())
        .append(TAB)
        .append(personTextRecord.getLastName())
        .append(TAB)
        .append(personTextRecord.getGender())
        .append(System.lineSeparator());

    return sb.toString();
  }
}
