package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Codificavel<T extends Number & Comparable<T>> extends Serializable {

	Comparator<Codificavel<?>> COMPARATOR_POR_CODIGO = ComparatorBuilder.build(Codificavel::getCodigo);

	String FIELD_NAME_CODIGO = "codigo";

	T getCodigo();

}