package de.continentale.vu.demo_jsr352;

import de.continentale.vu.demo_jsr352.domain.Person;

import java.util.List;

/**
 * CRUD operations on entity Person
 */
public interface PersonRepository {

    Person findById(Long id);
    List<Person> findAll();

    /**
     * Update existing person if record is set, otherwise create a new record.
     *
     * @param person entity to be saved
     * @return updated or created person
     */
    Person save(Person person);
    void delete(Long id);

    Person findByName(String firstName, String lastName);

    List<Long> findAllIds(final long minimumId);
}
