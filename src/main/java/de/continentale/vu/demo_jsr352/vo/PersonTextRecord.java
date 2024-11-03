package de.continentale.vu.demo_jsr352.vo;

/** Record in a text file. Conversion logic will be in the processor classes. */
public class PersonTextRecord {

  public static PersonTextRecord valueOf(int lineNumber, String[] fields)
      throws IllegalArgumentException {

    String firstName = null, lastName = null, age = null, gender = null;

    if (fields.length > 0) firstName = fields[0];
    if (fields.length > 1) lastName = fields[1];
    if (fields.length > 2) age = fields[2];
    if (fields.length > 3) gender = fields[3];

    return new PersonTextRecord(firstName, lastName, age, gender, lineNumber);
  }

  private final String firstName;
  private final String lastName;
  private final String age;
  private final String gender;
  private int lineNumber;

  public PersonTextRecord(String firstName, String lastName, String age, String gender) {
    this(firstName, lastName, age, gender, -1);
  }

  public PersonTextRecord(
      String firstName, String lastName, String age, String gender, int lineNumber) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.gender = gender;
    this.lineNumber = lineNumber;
  }

  @Override
  public String toString() {
    return "PersonTextRecord{"
        + "lineNumber='"
        + lineNumber
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", age="
        + age
        + ", gender='"
        + gender
        + '\''
        + '}';
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getAge() {
    return age;
  }

  public String getGender() {
    return gender;
  }
}
