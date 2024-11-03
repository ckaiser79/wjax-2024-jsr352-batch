package de.continentale.vu.demo_jsr352;

import de.continentale.vu.demo_jsr352.domain.Gender;
import de.continentale.vu.demo_jsr352.domain.Person;
import de.continentale.vu.testutils.LocalEntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

class PersonRepositoryImplTest {

  private static final Logger logger = getLogger(PersonRepositoryImplTest.class);

  private EntityManager entityManager;

  private PersonRepositoryImpl repository;

  @BeforeEach
  void setUp() {
      repository = new PersonRepositoryImpl();
      entityManager = LocalEntityManager.instance("defaultTEST");
      repository.setEntityManager(entityManager);
      entityManager.getTransaction().begin();
  }

  @AfterEach
  void tearDown() {
    entityManager.getTransaction().rollback();
  }

  @Test
  void createSomeData() {

    Person person;

    person = new Person();
    person.setFirstName("Jeff");
    person.setLastName("Smith");
    person.setGender(Gender.MALE);

    repository.save(person);

    person = new Person();
    person.setFirstName("Monica");
    person.setLastName("Miller");
    person.setGender(null);

    repository.save(person);

    List<Person> persons = repository.findAll();

    persons.forEach(p -> logger.info("Person: {}", p));

    assertEquals(2, persons.size());
  }
}
