package br.dev.juniorlatalisa.persistence;

import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.BeforeClass;

import br.dev.juniorlatalisa.model.ModelEntity;

public class ModelEntityTest extends TesteEntidade<ModelEntity> {

	@BeforeClass
	public static void beforeClass() {
		final String persistenceUnitName = "LatalisaTestRuntime";
		TesteEntidade.factory = new JPAFactoryBuilder() //
				.setProperty(AvailableSettings.JPA_JDBC_URL, // "javax.persistence.jdbc.url",
						"jdbc:h2:mem:latalisa_runtime;IGNORECASE=TRUE;DB_CLOSE_ON_EXIT=TRUE")
				.setProperty(AvailableSettings.JPA_JDBC_DRIVER, // "javax.persistence.jdbc.driver",
						org.h2.Driver.class.getName()) //
				.setProperty(AvailableSettings.DIALECT, // "hibernate.dialect",
						H2Dialect.class.getName()) //
				.setProperty(AvailableSettings.HBM2DDL_AUTO, // "hibernate.hbm2ddl.auto",
						"update") //
				.setPersistenceUnitName(persistenceUnitName) //
				.setPersistenceProviderClassName(HibernatePersistenceProvider.class.getName()) //
				.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL) //
				.setManagedClassNames(ModelEntity.class.getName()) //
				.build();
		TesteEntidade.beforeClass(persistenceUnitName);
	}

	@Override
	protected ModelEntity criar() {
		ModelEntity entity = new ModelEntity();
		entity.setCpf("11144477735");
		return persist(entity);
	}

	@Override
	protected ModelEntity alterar(ModelEntity entity) {
		entity.setEmail("teste@teste.com.br");
		return merge(entity);
	}

//
//
//	@Test(expected = ConstraintViolationException.class)
//	public void cfpNulo() {
//		ModelEntity entity = new ModelEntity();
//		Assert.assertNotNull(persist(entity).getCodigo());
//	}
//
//	@Test(expected = ConstraintViolationException.class)
//	public void cfpVazio() {
//		ModelEntity entity = new ModelEntity();
//		entity.setCpf("");
//		Assert.assertNotNull(persist(entity).getCodigo());
//	}
//
//	@Test(expected = ConstraintViolationException.class)
//	public void cfpInvalido() {
//		ModelEntity entity = new ModelEntity();
//		entity.setCpf("11144477733");
//		persist(entity);
//	}
//
//	@Test(expected = ConstraintViolationException.class)
//	public void emailInvalido() {
//		ModelEntity entity = new ModelEntity();
//		entity.setCpf("11144477735");
//		entity.setEmail("inválido");
//		persist(entity);
//	}
}