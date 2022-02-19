package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Identificavel<T extends Serializable> extends Serializable {

	@SuppressWarnings("unchecked")
	Comparator<Identificavel<? extends Comparable<? extends Serializable>>> COMPARATOR_POR_IDENTIFICADOR = //
			new ComparatorBuilder<Identificavel<? extends Comparable<? extends Serializable>>>()
					.add(Identificavel::getIdentificador,
							(value1, value2) -> ((Comparable<Serializable>) value1).compareTo(value2))
					.build();

	int DEFAULT_MAX_SIZE = 255;
	String NOT_EMPTY_MESSAGE = "{entidade.identificador.notempty}";

	T getIdentificador();

	default void setIdentificador(T identificador) {
		throw new PersistenceException();
	}
}