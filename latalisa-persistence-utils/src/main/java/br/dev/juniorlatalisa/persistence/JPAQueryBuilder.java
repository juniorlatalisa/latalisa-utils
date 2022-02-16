package br.dev.juniorlatalisa.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.persistence.JPAQuery.QueryStrategy;

public class JPAQueryBuilder {

	public JPAQueryBuilder(JPAQuery facade, QueryStrategy queryStrategy, String queryValue) {
		this.queryStrategy = queryStrategy;
		this.queryValue = queryValue;
		this.facade = facade;
	}

	private int maxResults = JPAQuery.MAX_RESULT_NONE;
	private int startResult = JPAQuery.START_RESULT_NONE;
	private Map<String, Object> params = new HashMap<>();

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
		this.params = params;
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

	public <T> T find() {
		List<T> result = facade.list(queryStrategy, queryValue, params, startResult, 1);
		return (result == null || result.isEmpty()) ? null : result.get(0);
	}

	public <T, R> R find(Function<T, R> function) {
		return function.apply(find());
	}

	public <T> Optional<T> optional() {
		return Optional.ofNullable(find());
	}

	public int execute() {
		return facade.execute(queryStrategy, queryValue, params);
	}

	public <T> List<T> list() {
		return facade.list(queryStrategy, queryValue, params, startResult, maxResults);
	}

	public <T> T single() {
		return facade.single(queryStrategy, queryValue, params);
	}

	public <T, R> R list(Function<List<T>, R> function) {
		return function.apply(list());
	}

	public static String load(InputStream is, Charset charset) {
		String sql;
		try {
			try {
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				sql = new String(buffer, charset);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
		if ((StandardCharsets.UTF_8.equals(charset)) && (sql.contains(JPAQuery.UTF8_BOM))) {
			sql = sql.replace(JPAQuery.UTF8_BOM, "");
		}
		return sql;
	}
}
