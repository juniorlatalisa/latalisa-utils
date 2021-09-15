package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

@FunctionalInterface
public interface Identificavel<T extends Serializable> extends Serializable {

	@SuppressWarnings("unchecked")
	final Comparator<Identificavel<?>> COMPARATOR_POR_IDENTIFICADOR = (cn1, cn2) -> //
	(cn1 == null || cn2 == null || cn1.getIdentificador() == null) ? 0
			: ((Comparable<? super Serializable>) cn1.getIdentificador()).compareTo(cn2.getIdentificador());

	final int DEFAULT_MAX_SIZE = 255;
	final String NOT_EMPTY_MESSAGE = "{entidade.identificador.notempty}";

	T getIdentificador();

	default void setIdentificador(T identificador) {
		throw new PersistenceException();
	}
}