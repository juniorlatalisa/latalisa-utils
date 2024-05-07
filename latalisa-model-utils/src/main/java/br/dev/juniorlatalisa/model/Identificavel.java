package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Identificavel<T extends Serializable & Comparable<T>> extends Serializable {

	Comparator<Identificavel<?>> COMPARATOR_POR_IDENTIFICADOR = //
			ComparatorBuilder.build(Identificavel::getIdentificador);

	int DEFAULT_MAX_SIZE = 255;
	String NOT_EMPTY_MESSAGE = "{entidade.identificador.notempty}";

	T getIdentificador();

	default void setIdentificador(T identificador) {
		throw new PersistenceException();
	}
}