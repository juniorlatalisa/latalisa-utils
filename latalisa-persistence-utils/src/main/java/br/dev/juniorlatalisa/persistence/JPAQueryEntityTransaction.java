package br.dev.juniorlatalisa.persistence;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

import javax.persistence.EntityTransaction;

public abstract class JPAQueryEntityTransaction extends JPAQuery {

	protected <T> T inEntityTransaction(Supplier<T> supplier) {
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

	@Override
	public int execute(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
		return inEntityTransaction(() -> super.execute(queryStrategy, queryValue, params));
	}

	@Override
	public <T extends Serializable> T create(T entity) {
		return inEntityTransaction(() -> super.create(entity));
	}

	@Override
	public <T extends Serializable> T update(T entity) {
		return inEntityTransaction(() -> super.update(entity));
	}

	@Override
	public <T extends Serializable> boolean delete(Class<T> entityClass, Serializable primaryKey) {
		return inEntityTransaction(() -> super.delete(entityClass, primaryKey));
	}
}
