package br.dev.juniorlatalisa.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * @author Junior Latalisa
 */
public abstract class JPAQuery {

	/**
	 * Quando um mecanismo de banco de dados não aceitar esse caracter no script em
	 * UTF-8 essa constante irá auxíliar para retira-lo.
	 * 
	 * @see <a href="https://pt.wikipedia.org/wiki/Marca_de_ordem_de_byte">Marca de
	 *      ordem de byte</a>
	 */
	public static final String UTF8_BOM = "\uFEFF";

	/**
	 * Constante para padronizar que o inicio do resultado é de forma padrão do
	 * mecanismo de banco de dados.
	 */
	public static final int START_RESULT_NONE = -1;

	/**
	 * Constante para padronizar que os registros devem ser retornados de forma
	 * padrão do mecanismo de banco de dados (sem limites).
	 */
	public static final int MAX_RESULT_NONE = -1;

	/**
	 * Constante para sinalizar que o número de linhas afetadas pelo script não pode
	 * ser aferida.
	 */
	public static final int EXECUTE_RESULT_NONE = -1;

	protected abstract EntityManager getEntityManager();

	/**
	 * @param <T>
	 * @param queryStrategy
	 * @param queryValue
	 * @param params
	 * @return
	 * @see Query#getSingleResult()
	 */
	@SuppressWarnings("unchecked")
	public <T> T single(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
		Query query = createQuery(queryStrategy, queryValue, params);
		setParams(queryStrategy, query, params, JPAQuery.START_RESULT_NONE, JPAQuery.MAX_RESULT_NONE);
		return (T) query.getSingleResult();
	}

	/**
	 * @param queryStrategy
	 * @param queryValue
	 * @param params
	 * @return
	 * @see Query#executeUpdate()
	 */
	public int execute(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
		Query query = createQuery(queryStrategy, queryValue, params);
		setParams(queryStrategy, query, params);
		return query.executeUpdate();
	}

	/**
	 * @param <T>
	 * @param queryStrategy
	 * @param queryValue
	 * @param params
	 * @param startResult
	 * @param maxResults
	 * @return
	 * @see Query#getResultList()
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> list(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params, int startResult,
			int maxResults) {
		Query query = createQuery(queryStrategy, queryValue, params);
		setParams(queryStrategy, query, params, startResult, maxResults);
		getEntityManager().clear();
		return query.getResultList();
	}

	protected void setParams(QueryStrategy queryStrategy, Query query, Map<String, Object> params, int startResult,
			int maxResults) {
		if (startResult != JPAQuery.START_RESULT_NONE) {
			query.setFirstResult(startResult);
		}
		if (maxResults != JPAQuery.MAX_RESULT_NONE) {
			query.setMaxResults(maxResults);
		}
		if (!(params == null || params.isEmpty())) {
			setParams(queryStrategy, query, params);
		}
	}

	protected void setParams(QueryStrategy queryStrategy, Query query, Map<String, Object> params) {
		params.forEach((name, value) -> query.setParameter(name, value));
	}

	protected Query createQuery(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
		switch (queryStrategy) {
		case DEFAULT:
			return getEntityManager().createQuery(queryValue);
		case NAMED:
			return getEntityManager().createNamedQuery(queryValue);
		case NATIVE:
			return getEntityManager().createNativeQuery(queryValue);
		default:
			throw new PersistenceException("QueryStrategy inválido: " + queryStrategy);
		}
	}

	/**
	 * @author Junior Latalisa
	 */
	public static enum QueryStrategy {
		/**
		 * Padrão.
		 * 
		 * @author Junior Latalisa
		 * @see EntityManager#createQuery(String)
		 */
		DEFAULT,
		/**
		 * Nomeada.
		 * 
		 * @author Junior Latalisa
		 * @see EntityManager#createNamedQuery(String)
		 */
		NAMED,
		/**
		 * Nativa.
		 * 
		 * @author Junior Latalisa
		 * @see EntityManager#createNativeQuery(String)
		 */
		NATIVE
	}

	/*
	 * 
	 * EntityManager Facade para o CRUD
	 * 
	 */
	public <T extends Serializable> T create(T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	public <T extends Serializable> T read(Class<T> entityClass, Serializable primaryKey) {
		getEntityManager().clear();
		return getEntityManager().find(entityClass, primaryKey);
	}

	public <T extends Serializable> T update(T entity) {
		return getEntityManager().merge(entity);
	}

	public <T extends Serializable> boolean delete(Class<T> entityClass, Serializable primaryKey) {
		try {
			getEntityManager().remove(getEntityManager().getReference(entityClass, primaryKey));
			return true;
		} catch (EntityNotFoundException e) {
			return false;
		}
	}
}