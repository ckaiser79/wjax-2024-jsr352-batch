package de.continentale.vu.demo_jsr352;

import static org.slf4j.LoggerFactory.getLogger;

import de.continentale.vu.demo_jsr352.domain.Person;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;

/** find methods return null or empty lists */
@ApplicationScoped
public class PersonRepositoryImpl implements PersonRepository {

  private static final Logger logger = getLogger(PersonRepositoryImpl.class);

  @PersistenceContext(unitName = "default")
  private EntityManager entityManager;

  @Override
  public Person findById(final Long id) {
    return entityManager.find(Person.class, id);
  }

  @Override
  public List<Person> findAll() {
    return entityManager.createQuery("select p from Person p", Person.class).getResultList();
  }

  @Override
  public Person save(final Person person) {
    entityManager.persist(person);
    entityManager.flush();

    final Person result = findById(person.getId());
    logger.trace("save(Person) result={}", result);

    return result;
  }

  @Override
  public void delete(final Long id) {

    logger.trace("delete(Long) id={}", id);

    entityManager
        .createQuery("delete from Person p where p.id = :id")
        .setParameter("id", id)
        .executeUpdate();
    entityManager.flush();


  }

  @Override
  public Person findByName(final String firstName, final String lastName) {
    final TypedQuery<Person> query =
        entityManager.createQuery(
            "select p from Person p where p.firstName = :firstName and p.lastName = :lastName",
            Person.class);
    query.setParameter("firstName", firstName);
    query.setParameter("lastName", lastName);
    return query.getSingleResult();
  }

  @Override
  public List<Long> findAllIds(final long minimumId) {

    return entityManager
        .createQuery("SELECT p.id FROM Person p order by p.id", Long.class)
        .getResultList();
  }

  /** inject dependency in tests */
  void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }
}
