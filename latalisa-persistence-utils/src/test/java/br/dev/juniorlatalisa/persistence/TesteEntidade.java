package br.dev.juniorlatalisa.persistence;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.validation.Validation;
import javax.validation.constraints.Size;

import org.junit.Assert;
import org.junit.Test;

import br.dev.juniorlatalisa.model.Codificavel;
import br.dev.juniorlatalisa.model.Copiavel;
import br.dev.juniorlatalisa.model.Identificavel;
import br.dev.juniorlatalisa.model.Nomeavel;
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
	 */
	public static void beforeClass(String persistenceUnitName) {
		beforeClass(persistenceUnitName, (Consumer<EntityManager>) null);
	}

	public static void beforeClass(String persistenceUnitName, InputStream loadQuery) {
		beforeClass(persistenceUnitName,
				entityManager -> createJPAQueryBuilder(QueryStrategy.NATIVE, loadQuery).execute());
	}

	public static void beforeClass(String persistenceUnitName, Consumer<EntityManager> consumer) {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory(persistenceUnitName);
			factory.createEntityManager().close();
		}
		if (facade == null) {
			log.info("Validador configurado: " + Validation.buildDefaultValidatorFactory().getValidator());
			final EntityManager entityManager = factory.createEntityManager();
			facade = JPAQueryEntityTransaction.create(entityManager);
			if (consumer != null) {
				consumer.accept(entityManager);
			}
		}
	}

	public static JPAQueryBuilder createJPAQueryBuilder(QueryStrategy queryStrategy, InputStream queryValue) {
		return createJPAQueryBuilder(queryStrategy, QueryBuilder.load(queryValue, StandardCharsets.UTF_8));
	}

	public static JPAQueryBuilder createJPAQueryBuilder(QueryStrategy queryStrategy, String queryValue) {
		return new JPAQueryBuilder(getJPAQuery(), queryStrategy, queryValue);
	}

	public static JPAQueryBuilder createNamedJPAQueryBuilder(String queryValue) {
		return new JPAQueryBuilder(getJPAQuery(), QueryStrategy.NAMED, queryValue);
	}

	protected abstract E criar();

	protected void detach(Object entity) {
		getJPAQuery().getEntityManager().detach(entity);
	}

	protected EntityManagerFactory getFactory() {
		return factory;
	}

	protected boolean testeCopiavel(Copiavel<E> destino) {
		return testeCopiavel(criar(), destino);
	}

	protected boolean testeCopiavel(E origem, Copiavel<E> destino) {
		destino.copiar(origem);
		return destino.equals(origem);
	}

	@SuppressWarnings("unchecked")
	protected void nomeVazio(Nomeavel entidade) {
		entidade.setNome("");
		getJPAQuery().update((E) entidade);
	}

	@SuppressWarnings("unchecked")
	protected void nomeNulo(Nomeavel entidade) {
		entidade.setNome(null);
		getJPAQuery().update((E) entidade);
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
		getJPAQuery().update((E) entidade);
	}

	protected Serializable getPrimaryKey(E entidade) {
		if (entidade instanceof Codificavel<?>) {
			return ((Codificavel<?>) entidade).getCodigo();
		}
		if (entidade instanceof Identificavel<?>) {
			return ((Identificavel<?>) entidade).getIdentificador();
		}
		throw new PersistenceException(String.format("Favor informaro método 'getPrimaryKey' para a classe %s", //
				entidade.getClass()));
	}

	protected E alterar(E entidade) {
		return getJPAQuery().update(entidade);
	}

	@SuppressWarnings("unchecked")
	protected E pesquisar(E entidade) {
		;
		return (E) getJPAQuery().read(entidade.getClass(), getPrimaryKey(entidade));
	}

	protected boolean remover(E entidade) {
		return getJPAQuery().delete(entidade.getClass(), getPrimaryKey(entidade));
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