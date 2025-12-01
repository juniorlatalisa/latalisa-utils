package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;

import br.dev.juniorlatalisa.builders.ComparatorBuilder;

@FunctionalInterface
public interface Nomeavel extends Serializable {

	Comparator<Nomeavel> COMPARATOR_POR_NOME = ComparatorBuilder.build(Nomeavel::getNome);

	Comparator<Nomeavel> COMPARATOR_POR_NOME_IGNORE_CASE = ComparatorBuilder.build(Nomeavel::getNome,
			String::compareToIgnoreCase);

	int DEFAULT_MAX_SIZE = 255;
	String NOT_EMPTY_MESSAGE = "{entidade.nome.notempty}";

	String getNome();

	default void setNome(@NotNull String nome) {
		throw new PersistenceException(nome);
	}
}