package br.dev.juniorlatalisa.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * @author Junior Latalisa
 * @since 2023-10
 */
public interface PrimaryKeyId<T extends Serializable> extends Entidade {

	T getId();

	static String toString(@NotNull PrimaryKeyId<?> entidade) {
		return String.format("%s [id=%s]", entidade.getClass().getSimpleName(), //
				entidade.getId() == null ? "null" : entidade.getId().toString());
	}
}
