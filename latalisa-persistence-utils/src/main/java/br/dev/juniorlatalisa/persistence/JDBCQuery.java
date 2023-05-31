package br.dev.juniorlatalisa.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.utils.ObjectUtils;

public abstract class JDBCQuery implements QueryFacade, AutoCloseable {

	protected abstract Connection getConnection();

	@Override
	public void close() {
		try {
			getConnection().close();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	protected Statement createStatement(int startResult) throws SQLException {
		return (QueryFacade.START_RESULT_NONE == startResult) ? getConnection().createStatement()
				: getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	protected PreparedStatement createPreparedStatement(String queryValue, int startResult) throws SQLException {
		return (QueryFacade.START_RESULT_NONE == startResult) ? getConnection().prepareStatement(queryValue)
				: getConnection().prepareStatement(queryValue, ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
	}

	protected PreparedStatement setParams(PreparedStatement preparedStatement, List<Object> params)
			throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			preparedStatement.setObject(i, params.get(i));
		}
		return preparedStatement;
	}

	public int execute(String queryValue, List<Object> params) {
		try (Statement statement = ObjectUtils.isEmpty(params) ? createStatement(QueryFacade.START_RESULT_NONE)
				: createPreparedStatement(queryValue, QueryFacade.START_RESULT_NONE)) {
			return (statement instanceof PreparedStatement) //
					? setParams((PreparedStatement) statement, params).executeUpdate() //
					: statement.executeUpdate(queryValue);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public static JDBCQuery create(Connection connection) {
		return new JDBCQuery() {
			@Override
			protected Connection getConnection() {
				return connection;
			}
		};
	}

}
