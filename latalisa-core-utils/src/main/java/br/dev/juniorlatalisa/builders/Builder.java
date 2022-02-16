package br.dev.juniorlatalisa.builders;

import java.util.function.Function;

/**
 * @author juniorlatalisa
 *
 */
@FunctionalInterface
public interface Builder<T> {

	T build();

	default <R> R build(Function<T, R> function) {
		return function.apply(build());
	}
}