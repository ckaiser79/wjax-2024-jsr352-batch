package de.continentale.vu.testutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * Lädt eine persistence Unit anhand ihres Namens im Classpath. Zweck ist ein einfacherer Zugriff
 * auf eine Persistence für den Test.
 *
 * <p><em>Decorator um einen EntityManager</em>
 */
public class LocalEntityManager implements EntityManager {

  private static Map<String, LocalEntityManager> instances = new HashMap<>();

  /**
   * Lazyloading eines entity managers. Soll über {@linkplain #removeAndCloseInstance(String)} nach
   * Nutzung geschlossen werden.
   *
   * @param  persistenceUnitName name aus der <code>persistence.xml</code>
   * @return instance for persistenceUnitName, never null
   */
  public static LocalEntityManager instance(final String persistenceUnitName) {

    if (!instances.containsKey(persistenceUnitName)) {
      final LocalEntityManager instance = new LocalEntityManager(persistenceUnitName);
      instances.put(persistenceUnitName, instance);
    }

    return instances.get(persistenceUnitName);
  }

  private final EntityManager targetEntityManager;

  /**
   * Load a EntityManager by name.
   *
   * @param persistenceUnitName name of persistence unit
   */
  public LocalEntityManager(final String persistenceUnitName) {

    final EntityManagerFactory factory =
        Persistence.createEntityManagerFactory(persistenceUnitName);
    targetEntityManager = factory.createEntityManager();
  }

  /**
   * Schließt einen optional existierenden offenen EntityManager, der über
   * {@linkplain #instance(String)} geholt worden ist.
   *
   * @param persistenceUnitName name aus der <code>persistence.xml</code>
   */
  public static void removeAndCloseInstance(final String persistenceUnitName) {
    if (instances.containsKey(persistenceUnitName)) {
      EntityManager em = instances.get(persistenceUnitName);
      if (em.isOpen()) em.close();

      instances.remove(persistenceUnitName);
    }
  }

  @Override
  public void persist(final Object entity) {
    targetEntityManager.persist(entity);
  }

  @Override
  public <T> T merge(final T entity) {
    return targetEntityManager.merge(entity);
  }

  @Override
  public void remove(final Object entity) {
    targetEntityManager.remove(entity);
  }

  @Override
  public <T> T find(final Class<T> entityClass, final Object primaryKey) {
    return targetEntityManager.find(entityClass, primaryKey);
  }

  @Override
  public <T> T find(
      final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
    return targetEntityManager.find(entityClass, primaryKey, properties);
  }

  @Override
  public <T> T find(
      final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
    return targetEntityManager.find(entityClass, primaryKey, lockMode);
  }

  @Override
  public <T> T find(
      final Class<T> entityClass,
      final Object primaryKey,
      final LockModeType lockMode,
      final Map<String, Object> properties) {
    return targetEntityManager.find(entityClass, primaryKey, lockMode, properties);
  }

  @Override
  public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
    return targetEntityManager.getReference(entityClass, primaryKey);
  }

  @Override
  public void flush() {
    targetEntityManager.flush();
  }

  @Override
  public void setFlushMode(final FlushModeType flushMode) {
    targetEntityManager.setFlushMode(flushMode);
  }

  @Override
  public FlushModeType getFlushMode() {
    return targetEntityManager.getFlushMode();
  }

  @Override
  public void lock(final Object entity, final LockModeType lockMode) {
    targetEntityManager.lock(entity, lockMode);
  }

  @Override
  public void lock(
      final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
    targetEntityManager.lock(entity, lockMode, properties);
  }

  @Override
  public void refresh(final Object entity) {
    targetEntityManager.refresh(entity);
  }

  @Override
  public void refresh(final Object entity, final Map<String, Object> properties) {
    targetEntityManager.refresh(entity, properties);
  }

  @Override
  public void refresh(final Object entity, final LockModeType lockMode) {
    targetEntityManager.refresh(entity, lockMode);
  }

  @Override
  public void refresh(
      final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
    targetEntityManager.refresh(entity, lockMode, properties);
  }

  @Override
  public void clear() {
    targetEntityManager.clear();
  }

  @Override
  public void detach(final Object entity) {
    targetEntityManager.detach(entity);
  }

  @Override
  public boolean contains(final Object entity) {
    return targetEntityManager.contains(entity);
  }

  @Override
  public LockModeType getLockMode(final Object entity) {
    return targetEntityManager.getLockMode(entity);
  }

  @Override
  public void setProperty(final String propertyName, final Object value) {
    targetEntityManager.setProperty(propertyName, value);
  }

  @Override
  public Map<String, Object> getProperties() {
    return targetEntityManager.getProperties();
  }

  @Override
  public Query createQuery(final String qlString) {
    return targetEntityManager.createQuery(qlString);
  }

  @Override
  public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
    return targetEntityManager.createQuery(criteriaQuery);
  }

  @Override
  public Query createQuery(final CriteriaUpdate updateQuery) {
    return targetEntityManager.createQuery(updateQuery);
  }

  @Override
  public Query createQuery(final CriteriaDelete deleteQuery) {
    return targetEntityManager.createQuery(deleteQuery);
  }

  @Override
  public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
    return targetEntityManager.createQuery(qlString, resultClass);
  }

  @Override
  public Query createNamedQuery(final String name) {
    return targetEntityManager.createNamedQuery(name);
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
    return targetEntityManager.createNamedQuery(name, resultClass);
  }

  @Override
  public Query createNativeQuery(final String sqlString) {
    return targetEntityManager.createNativeQuery(sqlString);
  }

  @Override
  public Query createNativeQuery(final String sqlString, final Class resultClass) {
    return targetEntityManager.createNativeQuery(sqlString, resultClass);
  }

  @Override
  public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
    return targetEntityManager.createNativeQuery(sqlString, resultSetMapping);
  }

  @Override
  public StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
    return targetEntityManager.createNamedStoredProcedureQuery(name);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
    return targetEntityManager.createStoredProcedureQuery(procedureName);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(
      final String procedureName, final Class... resultClasses) {
    return targetEntityManager.createStoredProcedureQuery(procedureName, resultClasses);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(
      final String procedureName, final String... resultSetMappings) {
    return targetEntityManager.createStoredProcedureQuery(procedureName, resultSetMappings);
  }

  @Override
  public void joinTransaction() {
    targetEntityManager.joinTransaction();
  }

  @Override
  public boolean isJoinedToTransaction() {
    return targetEntityManager.isJoinedToTransaction();
  }

  @Override
  public <T> T unwrap(final Class<T> cls) {
    return targetEntityManager.unwrap(cls);
  }

  @Override
  public Object getDelegate() {
    return targetEntityManager.getDelegate();
  }

  @Override
  public void close() {
    targetEntityManager.close();
  }

  @Override
  public boolean isOpen() {
    return targetEntityManager.isOpen();
  }

  @Override
  public EntityTransaction getTransaction() {
    return targetEntityManager.getTransaction();
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return targetEntityManager.getEntityManagerFactory();
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    return targetEntityManager.getCriteriaBuilder();
  }

  @Override
  public Metamodel getMetamodel() {
    return targetEntityManager.getMetamodel();
  }

  @Override
  public <T> EntityGraph<T> createEntityGraph(final Class<T> rootType) {
    return targetEntityManager.createEntityGraph(rootType);
  }

  @Override
  public EntityGraph<?> createEntityGraph(final String graphName) {
    return targetEntityManager.createEntityGraph(graphName);
  }

  @Override
  public EntityGraph<?> getEntityGraph(final String graphName) {
    return targetEntityManager.getEntityGraph(graphName);
  }

  @Override
  public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
    return targetEntityManager.getEntityGraphs(entityClass);
  }
}
