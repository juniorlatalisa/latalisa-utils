package br.dev.juniorlatalisa.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.Assert;
import org.junit.Test;

import br.dev.juniorlatalisa.persistence.model.EntityTest;

public class JPAFactoryBuilderTest {

	@Test
	public void build() {
		final EntityManagerFactory emf = new JPAFactoryBuilder() //
				.setProperty("javax.persistence.jdbc.url",
						"jdbc:h2:mem:vspersistence_runtime;IGNORECASE=TRUE;DB_CLOSE_ON_EXIT=TRUE")
				.setProperty("javax.persistence.jdbc.driver", org.h2.Driver.class.getName()) //
				.setProperty("hibernate.dialect", H2Dialect.class.getName()) //
				.setProperty("hibernate.hbm2ddl.auto", "update") //
				.setPersistenceUnitName("TestRuntime") //
				.setPersistenceProviderClassName(HibernatePersistenceProvider.class.getName()) //
				.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL) //
				.setManagedClassNames(EntityTest.class.getName()) //
				.build();
		try {
			Assert.assertTrue(emf.createEntityManager().isOpen());
		} finally {
			emf.close();
		}
	}

}
