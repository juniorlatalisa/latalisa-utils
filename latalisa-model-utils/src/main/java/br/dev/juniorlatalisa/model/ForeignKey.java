package br.dev.juniorlatalisa.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.PersistenceException;

public final class ForeignKey {

	private static final Set<String> names = new HashSet<>();

	public static ForeignKey add(String name, String table, String fields, String foreignTable, String foreignFields) {

		if (name == null || name.isEmpty()) {
			throw new PersistenceException("Invalid name!");
		}
		synchronized (names) {
			if (names.contains(name)) {
				throw new PersistenceException(String.format("Duplicate name '%s'!", name));
			}
		}
		return new ForeignKey(name, table, fields, foreignTable, foreignFields);

	}

	/**
	 * @param name
	 * @param table
	 * @param fields
	 * @param foreignTable
	 * @param foreignFields
	 */
	private ForeignKey(String name, String table, String fields, String foreignTable, String foreignFields) {
		super();
		this.name = name;
		this.table = table;
		this.fields = fields;
		this.foreignTable = foreignTable;
		this.foreignFields = foreignFields;
	}

	private final String name;
	private final String table;
	private final String fields;
	private final String foreignTable;
	private final String foreignFields;

	public String getName() {
		return name;
	}

	public String getTable() {
		return table;
	}

	public String getFields() {
		return fields;
	}

	public String getForeignTable() {
		return foreignTable;
	}

	public String getForeignFields() {
		return foreignFields;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fields, foreignFields, foreignTable, name, table);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForeignKey other = (ForeignKey) obj;
		return Objects.equals(fields, other.fields) && Objects.equals(foreignFields, other.foreignFields)
				&& Objects.equals(foreignTable, other.foreignTable) && Objects.equals(name, other.name)
				&& Objects.equals(table, other.table);
	}

	@Override
	public String toString() {
		return "ForeignKey [name=" + name + ", table=" + table + ", fields=" + fields + ", foreignTable=" + foreignTable
				+ ", foreignFields=" + foreignFields + "]";
	}
}