package br.dev.juniorlatalisa.function;

import java.util.function.Function;

/**
 * Permitir exceção à interface Function
 * 
 * @author Junior Latalisa
 *
 * @param <T> o tipo de entrada para a função
 * @param <R> o tipo do resultado da função
 * @param <E> o tipo da exceção ao executar o método
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Throwable> {

	/**
	 * Aplica esta função ao argumento fornecido.
	 * 
	 * @param t o argumento da função
	 * @return o resultado da função
	 * @throws E o tipo da exceção ao executar a função
	 */
	R apply(T t) throws E;

	/**
	 * Criar uma instância de java.util.function.Function a partir de um método com
	 * throws
	 * 
	 * @param <T>      o tipo de entrada para a função
	 * @param <R>      o tipo do resultado da função
	 * @param <E>      o tipo da exceção ao executar o método
	 * @param function
	 * @return uma instância de java.util.function.Function
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	static <T, R, E extends Exception> Function<T, R> as(FunctionWithException<T, R, E> function) {
		return value -> {
			try {
				return function.apply(value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
