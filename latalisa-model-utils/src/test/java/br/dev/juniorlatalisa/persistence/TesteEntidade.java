package br.dev.juniorlatalisa.persistence;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.Validation;
import javax.validation.constraints.Size;

import org.junit.Assert;
import org.junit.Test;

import br.dev.juniorlatalisa.model.Codificavel;
import br.dev.juniorlatalisa.model.Copiavel;
import br.dev.juniorlatalisa.model.EntidadeComposta;
import br.dev.juniorlatalisa.model.Identificavel;
import br.dev.juniorlatalisa.model.Nomeavel;
import br.dev.juniorlatalisa.model.NomeavelTest;
import br.dev.juniorlatalisa.persistence.JPAQuery.QueryStrategy;

public abstract class TesteEntidade<E extends Serializable> {

	protected static final Logger log = Logger.getLogger("TesteEntidade");

	protected static JPAQuery facade = null;
	protected static EntityManagerFactory factory = null;

	public static JPAQuery getJPAQuery() {
		return facade;
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	protected static void beforeClass(String persistenceUnitName) {
		beforeClass(persistenceUnitName, null);
	}

	/**
	 * &lt;dependency&gt;<br/>
	 * &emsp;&lt;groupId&gt;org.glassfish&lt;/groupId&gt;<br/>
	 * &emsp;&lt;artifactId&gt;javax.el&lt;/artifactId&gt;<br/>
	 * &emsp;&lt;version&gt;3.0.1-b08&lt;/version&gt;<br/>
	 * &emsp;&lt;scope&gt;test&lt;/scope&gt;<br/>
	 * &lt;/dependency&gt;<br/>
	 * &lt;dependency&gt;<br/>
	 * &emsp;&lt;groupId&gt;org.hibernate&lt;/groupId&gt;<br/>
	 * &emsp;&lt;artifactId&gt;hibernate-validator&lt;/artifactId&gt;<br/>
	 * &emsp;&lt;version&gt;6.0.2.Final&lt;/version&gt;<br/>
	 * &emsp;&lt;scope&gt;test&lt;/scope&gt;<br/>
	 * &lt;/dependency&gt;<br/>
	 * 
	 * @param persistenceUnitName
	 * @param loadQuery
	 */
	protected static void beforeClass(String persistenceUnitName, InputStream loadQuery) {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory(persistenceUnitName);
			factory.createEntityManager().close();
		}
		if (facade == null) {
			log.info("Validador configurado: " + Validation.buildDefaultValidatorFactory().getValidator());
			facade = new JPAQuery() {

				private final EntityManager entityManager = factory.createEntityManager();

				@Override
				protected EntityManager getEntityManager() {
					return entityManager;
				}

				@Override
				public int execute(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
					return inEntityTransaction(() -> super.execute(queryStrategy, queryValue, params));
				}

			};
			if (loadQuery != null) {
				createJPAQueryBuilder(QueryStrategy.NATIVE, loadQuery).execute();
			}
		}
	}

	public static JPAQueryBuilder createJPAQueryBuilder(QueryStrategy queryStrategy, InputStream queryValue) {
		return createJPAQueryBuilder(queryStrategy, JPAQueryBuilder.load(queryValue, StandardCharsets.UTF_8));
	}

	protected static JPAQueryBuilder createJPAQueryBuilder(QueryStrategy queryStrategy, String queryValue) {
		return new JPAQueryBuilder(facade, queryStrategy, queryValue);
	}

	protected static JPAQueryBuilder createNamedJPAQueryBuilder(String queryValue) {
		return new JPAQueryBuilder(facade, QueryStrategy.NAMED, queryValue);
	}

	private static final ThreadLocal<EntityManager> entityManagers = new ThreadLocal<>();

	protected static EntityManager getEntityManager() {
		EntityManager em;
		synchronized (entityManagers) {
			if ((em = entityManagers.get()) == null || !em.isOpen()) {
				entityManagers.set(em = factory.createEntityManager());
			}
		}
		return em;
	}

	protected abstract E criar();

	protected void detach(Object entity) {
		facade.getEntityManager().detach(entity);
	}

	protected E persist(E entity) {
		runnableEntityTransaction(() -> getEntityManager().persist(entity));
		return entity;
	}

	protected E merge(E entity) {
		return supplierEntityTransaction(() -> getEntityManager().merge(entity));
	}

	protected boolean remove(E entity) {
		try {
			runnableEntityTransaction(() -> getEntityManager()
					.remove(getEntityManager().getReference(entity.getClass(), getPrimaryKey((E) entity))));
		} catch (EntityNotFoundException e) {
			return false;
		}
		return true;
	}

	protected <T> T find(Class<T> entityClass, Object primaryKey) {
		getEntityManager().clear();
		return getEntityManager().find(entityClass, primaryKey);
	}

	protected void runnableEntityTransaction(Runnable runnable) {
		EntityTransaction transaction;
		(transaction = getEntityManager().getTransaction()).begin();
		try {
			runnable.run();
			transaction.commit();
		} catch (Throwable e) {
			transaction.rollback();
			throw e;
		}
	}

	protected static <T> T supplierEntityTransaction(Supplier<T> supplier) {
		EntityTransaction transaction;
		(transaction = getEntityManager().getTransaction()).begin();
		try {
			T retorno = supplier.get();
			transaction.commit();
			return retorno;
		} catch (Throwable e) {
			transaction.rollback();
			throw e;
		}
	}

	protected EntityManagerFactory getFactory() {
		return factory;
	}

	@SuppressWarnings("unchecked")
	protected void testeCopiavel(E origem, Copiavel<E> destino) {
		if (!remover(origem)) {
			throw new RuntimeException("Não removeu");
		}
		destino.copiar(origem);
		persist((E) destino);
	}

	@SuppressWarnings("unchecked")
	protected void nomeVazio(Nomeavel entidade) {
		entidade.setNome("");
		merge((E) entidade);
	}

	@SuppressWarnings("unchecked")
	protected void nomeNulo(Nomeavel entidade) {
		entidade.setNome(null);
		merge((E) entidade);
	}

	@SuppressWarnings("unchecked")
	protected void nomeSize(Nomeavel entidade) {
		Size size = null;
		{
			Class<?> _class = entidade.getClass();
			while (!_class.equals(Object.class)) {
				try {
					size = _class.getDeclaredField("nome").getAnnotation(Size.class);
					break;
				} catch (NoSuchFieldException | SecurityException e) {
					_class = _class.getSuperclass();
				}
			}
			if (size == null) {
				throw new RuntimeException("nomeSize fail: " + entidade);
			}
		}
		if (size.min() > 0) {
			entidade.setNome("");
		} else if (size.max() > 0) {
			entidade.setNome(new String(new char[1 + size.max()]));
		} else {
			entidade.setNome(null);
		}
		merge((E) entidade);
	}

	protected E alterar(E entidade) {
		NomeavelTest.preencher((Nomeavel) entidade);
		return merge(entidade);
	}

	protected Serializable getPrimaryKey(E entidade) {
		if (entidade instanceof Codificavel<?>) {
			return ((Codificavel<?>) entidade).getCodigo();
		}
		if (entidade instanceof EntidadeComposta) {
			return ((EntidadeComposta) entidade).getId();
		}
		if (entidade instanceof Identificavel<?>) {
			return ((Identificavel<?>) entidade).getIdentificador();
		}
		throw new IllegalAccessError("Unknow entity: " + entidade);
	}

	@SuppressWarnings("unchecked")
	protected E pesquisar(E entidade) {
		return (E) find(entidade.getClass(), getPrimaryKey(entidade));
	}

	protected boolean remover(E entidade) {
		return remove(entidade);
	}

	@Test
	public void basico() {
		E entidade1 = criar();

		E entidade2;

		if (!entidade1.equals(entidade2 = pesquisar(entidade1))) {
			Assert.fail("Entidades não são iguais [insert]");
			return;
		}

		if (entidade1.equals(alterar(entidade2))) {
			Assert.fail("Entidades são iguais");
			return;
		}

		if (!entidade2.equals(entidade2 = pesquisar(entidade1))) {
			Assert.fail("Entidades não são iguais [update]");
			return;
		}

		if (!((remover(entidade1)) && (pesquisar(entidade1) == null))) {
			Assert.fail("Objeto não foi removido");
		}
	}
}