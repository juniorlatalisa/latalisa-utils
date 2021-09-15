package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

@FunctionalInterface
public interface Ativavel extends Serializable {

	final Comparator<Ativavel> COMPARATOR_POR_ATIVO = (cn1, cn2) -> //
	(cn1 == null || cn2 == null || cn1.getAtivo() == null) ? 0 : cn1.getAtivo().compareTo(cn2.getAtivo());

	final String NOT_NULL_MESSAGE = "{entidade.ativo.notnull}";

	Boolean getAtivo();

	default void setAtivo(Boolean ativo) {
		throw new PersistenceException();
	}

	default boolean isAtivo() {
		return Boolean.TRUE.equals(getAtivo());
	}

}