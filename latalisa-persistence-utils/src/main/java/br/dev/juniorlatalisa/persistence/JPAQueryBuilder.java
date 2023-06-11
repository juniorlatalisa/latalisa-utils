package br.dev.juniorlatalisa.persistence;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.model.Entidade;
import br.dev.juniorlatalisa.persistence.JPAQuery.QueryStrategy;
import br.dev.juniorlatalisa.utils.ObjectUtils;

public class JPAQueryBuilder implements QueryBuilder {

	protected JPAQueryBuilder(JPAQuery facade, QueryStrategy queryStrategy, String queryValue) {
		this.queryStrategy = queryStrategy;
		this.queryValue = queryValue;
		this.facade = facade;
	}

	private int maxResults = QueryFacade.MAX_RESULT_NONE;
	private int startResult = QueryFacade.START_RESULT_NONE;

	private final Map<String, Object> params = new HashMap<>();
	private final QueryStrategy queryStrategy;
	private final String queryValue;

	private final JPAQuery facade;

	public JPAQueryBuilder setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	public JPAQueryBuilder setStartResult(int startResult) {
		this.startResult = startResult;
		return this;
	}

	public JPAQueryBuilder setParams(Map<String, Object> params) {
		this.params.clear();
		if (!ObjectUtils.isEmpty(params)) {
			this.params.putAll(params);
		}
		return this;
	}

	public JPAQueryBuilder addParams(Map<String, Object> params) {
		params.putAll(params);
		return this;
	}

	public JPAQueryBuilder setParam(String key, Object value) {
		params.put(key, value);
		return this;
	}

	@Override
	public JPAQueryBuilder clearParams() {
		params.clear();
		return this;
	}

	public JPAQueryBuilder removeParam(String key) {
		params.remove(key);
		return this;
	}

	public JPAQueryBuilder clearMaxResults() {
		return setMaxResults(JPAQuery.MAX_RESULT_NONE);
	}

	public JPAQueryBuilder clearStartResult() {
		return setStartResult(JPAQuery.START_RESULT_NONE);
	}

	public <T> T single() {
		return facade.single(queryStrategy, queryValue, params);
	}

	@Override
	public <T> T find() {
		List<T> result = facade.list(queryStrategy, queryValue, params, startResult, 1);
		return (result == null || result.isEmpty()) ? null : result.get(0);
	}

	@Override
	public int execute() {
		return facade.execute(queryStrategy, queryValue, params);
	}

	@Override
	public <T> List<T> list() {
		return facade.list(queryStrategy, queryValue, params, startResult, maxResults);
	}

	@Override
	public <T> Iterator<T> iterator() {
		throw new PersistenceException("Iterator is not suported.");
	}

	public static JPAQueryBuilder create(JPAQuery facade, QueryStrategy queryStrategy, String queryValue) {
		return new JPAQueryBuilder(facade, queryStrategy, queryValue);
	}

	public static JPAQueryBuilder create(JPAQuery facade, QueryStrategy queryStrategy, InputStream is,
			Charset charset) {
		return new JPAQueryBuilder(facade, queryStrategy, QueryBuilder.load(is, charset));
	}

	public static JPAQueryBuilder create(JPAQuery facade, Class<? extends Entidade> entityClass, String where) {
		final Entity entity = entityClass.getAnnotation(Entity.class);
		return new JPAQueryBuilder(facade, QueryStrategy.DEFAULT, String.format("select e from %s e %s",
				entity == null ? entityClass.getSimpleName() : entity.name(), where));
	}

	public static JPAQueryBuilder create(JPAQuery facade, Class<? extends Entidade> entityClass) {
		return create(facade, entityClass, "");
	}

}
