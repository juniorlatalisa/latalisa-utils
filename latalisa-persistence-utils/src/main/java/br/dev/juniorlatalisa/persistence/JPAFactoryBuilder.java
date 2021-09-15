package br.dev.juniorlatalisa.persistence;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * Classe focada na utilização de JPA sem o arquivo persistence.xml ou sem o
 * persistenceUnit configurado no persistence.xml existente. Será possivel
 * utilizar um {@link DataSource} ou configurações manuais. Quando for utilizada
 * com CDI, talvez tenha que atribuir bean-discovery-mode="all" no arquivo
 * beans.xml.<br>
 * Exemplo sem {@link DataSource}:<br>
 * 
 * <pre class="prettyprint linenums">
 * <code class="language-java"
 * >private EntityManagerFactory emf = new EntityManagerFactoryBuilder()
 *      .setProperty("javax.persistence.jdbc.url", new StringBuilder("jdbc:derby:")
 *          .append(System.getProperty("java.io.tmpdir"))
 *          .append("VSPersistencia;create=true;")
 *      .toString())
 *      .setProperty("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver")
 *      .setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect")
 *      .setProperty("hibernate.hbm2ddl.auto", "update")
 *      .setPersistenceProviderClassName("org.hibernate.jpa.HibernatePersistenceProvider")
 *      .setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
 *      .setPersistenceUnitName("VSPersistencia")
 *      .setManagedClassNames(Cliente.class.getName())
 * .getEntityManagerFactory();</code>
 * </pre>
 * 
 * As configurações podem variar dependendo da implemtação escolhida (no exemplo
 * Hibernate), ambiente (Tomcat, JBoss, etc...) e do
 * PersistenceUnitTransactionType escolhido.
 * 
 * @author Junior Latalisa
 * @since 3.0
 * @see PersistenceProvider
 * @see PersistenceUnitInfo
 * @see PersistenceUnitTransactionType
 */
public class JPAFactoryBuilder {

	private Properties properties;
	private DataSource dataSource;
	private ClassLoader classLoader;
	private String persistenceUnitName;
	private List<String> managedClassNames;
	private String persistenceProviderClassName;
	private PersistenceUnitTransactionType transactionType;

	/**
	 * @return {@link DataSource}
	 * @see PersistenceUnitInfo#getJtaDataSource()
	 * @see PersistenceUnitInfo#getNonJtaDataSource()
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Caso o PersistenceUnitTransactionType seja RESOURCE_LOCAL o datasource será
	 * usado pelo método {@link PersistenceUnitInfo#getNonJtaDataSource()}, caso
	 * seja JTA será usado pelo método
	 * {@link PersistenceUnitInfo#getJtaDataSource()}
	 * 
	 * @param dataSource {@link DataSource}
	 * @return A própria instância do deste builder.
	 * @see PersistenceUnitInfo#getJtaDataSource()
	 * @see PersistenceUnitInfo#getNonJtaDataSource()
	 * @see PersistenceUnitTransactionType#RESOURCE_LOCAL
	 * @see PersistenceUnitTransactionType#JTA
	 */
	public JPAFactoryBuilder setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	/**
	 * @see PersistenceUnitInfo#getPersistenceUnitName()
	 */
	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	/**
	 * @return A própria instância do deste builder.
	 * @see PersistenceUnitInfo#getPersistenceUnitName()
	 */
	public JPAFactoryBuilder setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
		return this;
	}

	/**
	 * @see PersistenceUnitInfo#getPersistenceUnitName()
	 */
	public String getPersistenceProviderClassName() {
		return persistenceProviderClassName;
	}

	/**
	 * @return A própria instância do deste builder.
	 * @see PersistenceUnitInfo#getPersistenceUnitName()
	 */
	public JPAFactoryBuilder setPersistenceProviderClassName(String persistenceProviderClassName) {
		this.persistenceProviderClassName = persistenceProviderClassName;
		return this;
	}

	/**
	 * @return A própria instância do deste builder.
	 * @see #setPersistenceProviderClassName(String)
	 */
	public JPAFactoryBuilder setPersistenceProviderClass(
			Class<? extends PersistenceProvider> persistenceProviderClass) {
		return setPersistenceProviderClassName(persistenceProviderClass.getName());
	}

	/**
	 * @see PersistenceUnitInfo#getClassLoader()
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * @return A própria instância do deste builder.
	 * @see PersistenceUnitInfo#getClassLoader()
	 */
	public JPAFactoryBuilder setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	/**
	 * @see PersistenceUnitInfo#getTransactionType()
	 */
	public PersistenceUnitTransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType {@link PersistenceUnitTransactionType}
	 * @return A própria instância do deste builder.
	 * @see PersistenceUnitInfo#getTransactionType()
	 * @see PersistenceUnitTransactionType#RESOURCE_LOCAL
	 * @see PersistenceUnitTransactionType#JTA
	 */
	public JPAFactoryBuilder setTransactionType(PersistenceUnitTransactionType transactionType) {
		this.transactionType = transactionType;
		return this;
	}

	/**
	 * @see PersistenceUnitInfo#getManagedClassNames()
	 */
	public List<String> getManagedClassNames() {
		return getManagedClassNames(false);
	}

	protected List<String> getManagedClassNames(boolean create) {
		if ((create) && (managedClassNames == null)) {
			managedClassNames = createList();
		}
		return managedClassNames;
	}

	protected List<String> createList() {
		return new ArrayList<String>();
	}

	/**
	 * Substitui a lista de classes gerenciadas pela nova lista. Para adicionar uma
	 * lista favor utilizar o método {@link #addManagedClassNames(List)}
	 * 
	 * @param managedClassNames Nova lista das classes gerenciadas
	 * @return A própria instância do deste builder.
	 */
	public JPAFactoryBuilder setManagedClassNames(List<String> managedClassNames) {
		this.managedClassNames = managedClassNames;
		return this;
	}

	/**
	 * Substitui a lista de classes gerenciadas pelo(s) nome(s) passado(s). Para
	 * adicionar favor utilizar o método {@link #addManagedClassNames(String...)}
	 * 
	 * @param managedClassNames Nome(s) da(s) classe(s) gerenciada(s)
	 * @return A própria instância do deste builder.
	 */
	public JPAFactoryBuilder setManagedClassNames(String... managedClassNames) {
		this.managedClassNames = Arrays.asList(managedClassNames);
		return this;
	}

	/**
	 * Adiciona a nova lista ao builder.
	 * 
	 * @param managedClassNames Nova lista das classes gerenciadas
	 * @return A própria instância do deste builder.
	 * @see List#addAll(java.util.Collection)
	 */
	public JPAFactoryBuilder addManagedClassNames(List<String> managedClassNames) {
		getManagedClassNames(true).addAll(managedClassNames);
		return this;
	}

	/**
	 * Adiciona o(s) nome(s) ao builder.
	 * 
	 * @param managedClassNames Nome(s) da(s) classe(s) gerenciada(s)
	 * @return A própria instância do deste builder.
	 * @see List#addAll(java.util.Collection)
	 */
	public JPAFactoryBuilder addManagedClassNames(String... managedClassNames) {
		getManagedClassNames(true).addAll(Arrays.asList(managedClassNames));
		return this;
	}

	/**
	 * Atribui null a lista de nome das classes gerenciadas.
	 * 
	 * @return A própria instância do deste builder.
	 */
	public JPAFactoryBuilder clearManagedClassNames() {
		managedClassNames = null;
		return this;
	}

	/**
	 * @see PersistenceUnitInfo#getProperties()
	 * @see PersistenceProvider#createContainerEntityManagerFactory(PersistenceUnitInfo,
	 *      java.util.Map)
	 */
	public Properties getProperties() {
		return (properties == null) ? properties = new Properties() : properties;
	}

	/**
	 * Substitui o mapa de propriedades pelo novo mapa. Para adicionar propriedades
	 * favor utilizar os métodos {@link #addProperties(Properties)} ou
	 * {@link #setProperty(String, String)}
	 * 
	 * @param properties Novo mapa de propriedades.
	 * @return A própria instância do deste builder.
	 */
	public JPAFactoryBuilder setProperties(Properties properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Adiciona o novo mapa de propriedades ao builder.
	 * 
	 * @param properties Novo mapa de propriedades.
	 * @return A própria instância do deste builder.
	 * @see Properties#putAll(java.util.Map)
	 */
	public JPAFactoryBuilder addProperties(Properties properties) {
		getProperties().putAll(properties);
		return this;
	}

	/**
	 * Adiciona a propriedade ao builder.
	 * 
	 * @param key   Chave da propriedade.
	 * @param value Valor da propriedade.
	 * @return A própria instância do deste builder.
	 * @see Properties#setProperty(String, String)
	 */
	public JPAFactoryBuilder setProperty(String key, String value) {
		getProperties().setProperty(key, value);
		return this;
	}

	/**
	 * Atribui null ao mapa de propriedades.
	 * 
	 * @return A própria instância do deste builder.
	 */
	public JPAFactoryBuilder clearProperties() {
		this.properties = null;
		return this;
	}

	/**
	 * Cria um {@link EntityManagerFactory} conforme os atributos do builder.
	 * 
	 * @return Conforme descrito em:
	 *         {@link PersistenceProvider#createContainerEntityManagerFactory(PersistenceUnitInfo, java.util.Map)}
	 * @see PersistenceUnitInfo
	 * @see PersistenceProvider#createContainerEntityManagerFactory(PersistenceUnitInfo,
	 *      java.util.Map)
	 */
	public EntityManagerFactory build() {
		PersistenceProvider pp;
		try {
			pp = (PersistenceProvider) (Class.forName(getPersistenceProviderClassName())).getDeclaredConstructor()
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new PersistenceException(e);
		}
		return pp.createContainerEntityManagerFactory(createPersistenceUnitInfo(), getProperties());
	}

	protected PersistenceUnitInfo createPersistenceUnitInfo() {
		return new PersistenceUnitInfo() {

			private DataSource dataSource = JPAFactoryBuilder.this.getDataSource();
			private ClassLoader classLoader = JPAFactoryBuilder.this.getClassLoader();
			private String persistenceUnitName = JPAFactoryBuilder.this.getPersistenceUnitName();
			private PersistenceUnitTransactionType transactionType = JPAFactoryBuilder.this.getTransactionType();
			private String persistenceProviderClassName = JPAFactoryBuilder.this.getPersistenceProviderClassName();
			private List<String> managedClassNames = new ArrayList<String>(
					JPAFactoryBuilder.this.getManagedClassNames(true));

			@Override
			public String getPersistenceUnitName() {
				return persistenceUnitName;
			}

			@Override
			public String getPersistenceProviderClassName() {
				return persistenceProviderClassName;
			}

			@Override
			public PersistenceUnitTransactionType getTransactionType() {
				return transactionType;
			}

			@Override
			public DataSource getJtaDataSource() {
				return (PersistenceUnitTransactionType.JTA.equals(transactionType)) ? dataSource : null;
			}

			@Override
			public DataSource getNonJtaDataSource() {
				return (PersistenceUnitTransactionType.RESOURCE_LOCAL.equals(transactionType)) ? dataSource : null;
			}

			@Override
			public List<String> getMappingFileNames() {
				return null;// mappingFileNames;
			}

			@Override
			public List<URL> getJarFileUrls() {
				return null;// jarFileUrls;
			}

			@Override
			public URL getPersistenceUnitRootUrl() {
				return null;// persistenceUnitRootUrl;
			}

			@Override
			public List<String> getManagedClassNames() {
				return managedClassNames;
			}

			@Override
			public boolean excludeUnlistedClasses() {
				return (!((getManagedClassNames() == null) || (getManagedClassNames().isEmpty())));
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				return null;// sharedCacheMode;
			}

			@Override
			public ValidationMode getValidationMode() {
				return null;// validationMode;
			}

			@Override
			public Properties getProperties() {
				return null;// properties;
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				return null;// persistenceXMLSchemaVersion;
			}

			@Override
			public ClassLoader getClassLoader() {
				return classLoader;
			}

			@Override
			public void addTransformer(ClassTransformer transformer) {
				// TODO Verificar e implementar
				throw new PersistenceException("Implementar addTransformer");
			}

			@Override
			public ClassLoader getNewTempClassLoader() {
				return getClass().getClassLoader();
			}
		};
	}
}