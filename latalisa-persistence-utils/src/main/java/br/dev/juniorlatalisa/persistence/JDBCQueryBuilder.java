package br.dev.juniorlatalisa.persistence;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JDBCQueryBuilder implements QueryBuilder {

	public JDBCQueryBuilder(JDBCQuery facade, String queryValue) {
		this.facade = facade;
		this.queryValue = queryValue;
	}

	private final String queryValue;
	private final JDBCQuery facade;

	private List<Object> params = new LinkedList<>();

	@Override
	public JDBCQueryBuilder clearParams() {
		this.params.clear();
		return this;
	}

	@Override
	public <T> T find() {
		return null;
	}

	@Override
	public int execute() {
		return facade.execute(queryValue, params);
	}

	@Override
	public <T> List<T> list() {
		return null;
	}

	@Override
	public <T> Iterator<T> iterator() {
		return null;
	}
}
