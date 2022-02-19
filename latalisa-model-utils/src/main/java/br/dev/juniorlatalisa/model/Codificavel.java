package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Codificavel<T extends Number> extends Serializable {

	@SuppressWarnings("unchecked")
	Comparator<Codificavel<? extends Number>> COMPARATOR_POR_CODIGO = new ComparatorBuilder<Codificavel<? extends Number>>()
			.add(value -> (Comparable<Object>) value.getCodigo(), Comparable::compareTo).build();

	String FIELD_NAME_CODIGO = "codigo";

	T getCodigo();

}