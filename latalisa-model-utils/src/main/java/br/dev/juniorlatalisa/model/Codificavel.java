package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface Codificavel<T extends Number> extends Serializable {

	@SuppressWarnings("unchecked")
	Comparator<Codificavel<? extends Number>> COMPARATOR_POR_CODIGO = (cn1,
			cn2) -> (cn1 == null || cn2 == null || cn1.getCodigo() == null) ? 0
					: ((Comparable<Number>) cn1.getCodigo()).compareTo(cn2.getCodigo());

	String FIELD_NAME_CODIGO = "codigo";

	T getCodigo();

}