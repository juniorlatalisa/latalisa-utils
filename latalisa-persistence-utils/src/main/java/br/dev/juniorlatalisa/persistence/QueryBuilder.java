package br.dev.juniorlatalisa.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.PersistenceException;

public interface QueryBuilder {

	int execute();

	<T> T find();

	<T> List<T> list();

	<T> Iterator<T> iterator();

	QueryBuilder clearParams();

	default <T, R> R list(Function<List<T>, R> function) {
		return function.apply(list());
	}

	default <T> Optional<T> optional() {
		return Optional.ofNullable(find());
	}

	default <T, R> R find(Function<T, R> function) {
		return function.apply(find());
	}

	static String load(InputStream is, Charset charset) {
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
		if ((StandardCharsets.UTF_8.equals(charset)) && (sql.contains(QueryFacade.UTF8_BOM))) {
			sql = sql.replace(QueryFacade.UTF8_BOM, "");
		}
		return sql;
	}

}
