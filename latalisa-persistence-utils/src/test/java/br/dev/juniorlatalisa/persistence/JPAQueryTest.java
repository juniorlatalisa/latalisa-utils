package br.dev.juniorlatalisa.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.dev.juniorlatalisa.persistence.JPAQuery.QueryStrategy;
import br.dev.juniorlatalisa.persistence.model.EntityTest;

public class JPAQueryTest {

	private static EntityManagerFactory factory;
	private static JPAQuery query;
	private static EntityTest entity;

	@BeforeClass
	public static void beforeClass() {
		factory = Persistence.createEntityManagerFactory("JPATest");

		entity = new EntityTest();
		entity.setName("Junior Latalisa");
		entity.setValue(1.99);

		EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(entity);
			em.getTransaction().commit();
			em.detach(entity);
		} finally {
			em.close();
		}

		query = new JPAQuery() {

			private final EntityManager entityManager = factory.createEntityManager();

			@Override
			protected EntityManager getEntityManager() {
				return entityManager;
			}

			@Override
			public int execute(QueryStrategy queryStrategy, String queryValue, Map<String, Object> params) {
				EntityTransaction transaction;
				(transaction = entityManager.getTransaction()).begin();
				try {
					int retorno = super.execute(queryStrategy, queryValue, params);
					transaction.commit();
					return retorno;
				} catch (PersistenceException e) {
					transaction.rollback();
					throw e;
				}
			}

		};
	}

	@AfterClass
	public static void afterClass() {
		factory.close();
	}

	protected static JPAQueryBuilder createJPAQueryBuilder(QueryStrategy queryStrategy, String queryValue) {
		return new JPAQueryBuilder(query, queryStrategy, queryValue);
	}

	@Test
	public void find() {
		EntityTest retorno = createJPAQueryBuilder(QueryStrategy.DEFAULT, //
				"select e from EntityTest e where e.id = :id") //
						.setParam("id", entity.getId()) //
						.find();
		Assert.assertEquals(entity.getId(), retorno.getId());
	}

	@Test
	public void update1() {
		Assert.assertEquals(createJPAQueryBuilder(QueryStrategy.NAMED, EntityTest.UPDATE1)//
				.setParam("id", entity.getId()) //
				.setParam("name", "Nome Update 1") //
				.execute(), 1);
	}

	@Test
	public void update2() {
		Assert.assertEquals(createJPAQueryBuilder(QueryStrategy.NAMED, EntityTest.UPDATE2)//
				.setParam("id", entity.getId()) //
				.setParam("name", "Nome Update 2") //
				.execute(), 1);
	}

	@Test
	public void nativo() {
		Assert.assertEquals(createJPAQueryBuilder(QueryStrategy.NATIVE, //
				"update entity_test set name = :name where id = :id")//
						.setParam("id", entity.getId()) //
						.setParam("name", entity.getName()) //
						.execute(),
				1);
	}

}
