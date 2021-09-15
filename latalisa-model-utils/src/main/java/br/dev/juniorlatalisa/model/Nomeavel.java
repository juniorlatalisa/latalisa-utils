package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;

@FunctionalInterface
public interface Nomeavel extends Serializable {

	final Comparator<Nomeavel> COMPARATOR_POR_NOME = (cn1, cn2) -> //
	(cn1 == null || cn2 == null || cn1.getNome() == null) ? 0 : cn1.getNome().compareTo(cn2.getNome());
	final Comparator<Nomeavel> COMPARATOR_POR_NOME_IGNORE_CASE = (cn1, cn2) -> //
	(cn1 == null || cn2 == null || cn1.getNome() == null) ? 0 : cn1.getNome().compareToIgnoreCase(cn2.getNome());

	final int DEFAULT_MAX_SIZE = 255;
	final String NOT_EMPTY_MESSAGE = "{entidade.nome.notempty}";

	String getNome();

	default void setNome(String nome) {
		throw new PersistenceException(nome);
	}
}