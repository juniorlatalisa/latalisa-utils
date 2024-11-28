package br.dev.juniorlatalisa.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;

import br.dev.juniorlatalisa.model.Entidade;
import br.dev.juniorlatalisa.utils.ObjectUtils;

/**
 * @author Junior Latalisa
 * @since 2023-10
 */
public abstract class JPAQueryMultiDataBase<E extends Entidade> extends JPAQuery {

	protected abstract DataSourceLookup getDataSourceLookup();

	protected abstract Map<Class<? extends E>, Class<? extends E>> getMappedClasses();

	protected abstract ThreadLocal<String> getContextThreadLocal();

	protected abstract ThreadLocal<EntityManager> getEntityManagerThreadLocal();

	protected abstract Map<String, EntityManagerFactory> getFactories();

	@Override
	public EntityManager getEntityManager() {
		EntityManager em = getEntityManagerThreadLocal().get();
		if (em == null) {
			throw new PersistenceException("Contexto não foi definido");
		}
		return em;
	}

	protected JPAFactoryBuilder createJPAFactoryBuilder(DataSource dataSource, String persistenceUnitName,
			boolean generateStatistics, boolean eventsLog, boolean jmxEnabled) {
		return new JPAFactoryBuilder() //
				.setDataSource(dataSource) //
				.setPersistenceUnitName(persistenceUnitName) //
				.setPersistenceProviderClassName(HibernatePersistenceProvider.class.getName()) //
				.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL) //
				.setProperty("hibernate.generate_statistics", Boolean.toString(generateStatistics)) //
				.setProperty("hibernate.session.events.log", Boolean.toString(eventsLog)) //
				.setProperty("hibernate.jmx.enabled", Boolean.toString(jmxEnabled)) //
		;
	}

	protected JPAFactoryBuilder createJPAFactoryBuilder(DataSource dataSource, String persistenceUnitName) {
		return createJPAFactoryBuilder(dataSource, persistenceUnitName, true, false, true);
	}

	protected EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
		final DataSource dataSource;
		try {
			dataSource = getDataSourceLookup().doLookupDataSource(persistenceUnitName);
		} catch (NamingException e) {
			throw new PersistenceException(e);
		}
		final List<String> managedClassNames = getMappedClasses().values().stream().map(Class::getCanonicalName)
				.sorted().collect(Collectors.toList());
		return createJPAFactoryBuilder(dataSource, persistenceUnitName) //
				.setManagedClassNames(managedClassNames) //
				.build();
	}

	protected EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
		final var factories = getFactories();
		final EntityManagerFactory factory;
		synchronized (factories) {
			factory = factories.computeIfAbsent(persistenceUnitName, this::createEntityManagerFactory);
		}
		return factory;
	}

	protected <T> T run(Supplier<T> command) {
		return run(command, false);
	}

	protected <T> T run(Supplier<T> command, boolean transaction) {
		final String contexto = getContextThreadLocal().get();
		if (ObjectUtils.isEmpty(contexto)) {
			throw new PersistenceException("Contexto não foi definido");
		}
		final var entityManager = getEntityManagerThreadLocal();
		final EntityManager em = getEntityManagerFactory(contexto).createEntityManager();
		try {
			entityManager.set(em);
			try {
				return transaction ? transaction(em, command) : command.get();
			} finally {
				entityManager.remove();
			}
		} finally {
			em.close();
		}
	}

	protected <T> T transaction(EntityManager em, Supplier<T> command) {
		final var transaction = em.getTransaction();
		transaction.begin();
		try {
			T retorno = command.get();
			transaction.commit();
			return retorno;
		} catch (Throwable e) {
			transaction.rollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends Serializable> Class<T> resolveClass(Class<T> entityClass) {
		if (getMappedClasses().containsKey(entityClass)) {
			return (Class<T>) getMappedClasses().get(entityClass);
		}
		throw new PersistenceException(String.format("Entidade não conhecida %s", entityClass.getSimpleName()));
	}

	@Override
	public <T extends Serializable> T read(Class<T> entityClass, Serializable primaryKey) {
		return run(() -> super.read(resolveClass(entityClass), primaryKey));
	}

	@Override
	public <T> List<T> list(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params, int startResult,
			int maxResults) {
		return run(() -> super.list(queryStrategy, queryValue, params, startResult, maxResults));
	}

	@Override
	public <T extends Serializable> T create(T entity) {
		return run(() -> super.create(entity), true);
	}

	@Override
	public <T extends Serializable> T update(T entity) {
		return run(() -> super.update(entity), true);
	}

}
