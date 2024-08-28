package br.dev.juniorlatalisa.persistence;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author Junior Latalisa
 * @since 2024-08
 */
public interface DataSourceLookup {

	DataSource doLookupDataSource(String contexto) throws NamingException;

}
