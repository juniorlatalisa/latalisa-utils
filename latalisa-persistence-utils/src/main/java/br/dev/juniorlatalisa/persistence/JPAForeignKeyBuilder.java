package br.dev.juniorlatalisa.persistence;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import br.dev.juniorlatalisa.builders.Builder;
import br.dev.juniorlatalisa.model.ForeignKey;
import br.dev.juniorlatalisa.persistence.JPAQuery.QueryStrategy;

public class JPAForeignKeyBuilder implements Builder<Integer> {

	public JPAForeignKeyBuilder(JPAQuery facade) {
		this.facade = facade;
		this.foreignKeys = new HashSet<>();
	}

	private final JPAQuery facade;
	private final Set<ForeignKey> foreignKeys;

	protected JPAQuery getFacade() {
		return facade;
	}

	protected Set<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public JPAForeignKeyBuilder add(ForeignKey... foreignKeys) {
		getForeignKeys().addAll(Arrays.asList(foreignKeys));
		return this;
	}

	public JPAForeignKeyBuilder add(Collection<ForeignKey> foreignKeys) {
		getForeignKeys().addAll(foreignKeys);
		return this;
	}

	protected String buildIfNotExists(ForeignKey foreignKey) {
		return String.format("alter table %s add constraint if not exists %s foreign key(%s) references %s(%s);",
				foreignKey.getTable(), foreignKey.getName(), foreignKey.getFields(), foreignKey.getForeignTable(),
				foreignKey.getForeignFields());
	}

	@Override
	public Integer build() {
		var sql = getForeignKeys().stream().map(this::buildIfNotExists).sorted(String::compareTo)
				.collect(Collectors.joining("\n"));
		Logger.getLogger("JPAForeignKeyBuilder").info(() -> "Start JPAForeignKeyBuilder...\n" + sql);
		return getFacade().execute(QueryStrategy.NATIVE, sql, Map.of());
	}
}