package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Ativavel extends Serializable {

	Comparator<Ativavel> COMPARATOR_POR_ATIVO = ComparatorBuilder.build(Ativavel::getAtivo);

	String NOT_NULL_MESSAGE = "{entidade.ativo.notnull}";

	Boolean getAtivo();

	default void setAtivo(Boolean ativo) {
		throw new PersistenceException();
	}

	default boolean isAtivo() {
		return Boolean.TRUE.equals(getAtivo());
	}

}