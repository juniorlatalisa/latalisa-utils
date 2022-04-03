package br.dev.juniorlatalisa.function;

/**
 * Permitir exceção à interface Function
 * @author Junior Latalisa
 *
 * @param <T>
 * @param <R>
 * @param <E> o tipo da exceção ao executar o método
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Throwable> {


	/**
	 * Aplica esta função ao argumento fornecido.
	 * @param t o argumento da função
	 * @return o resultado da função
	 * @throws E o tipo da exceção ao executar a função
	 */
	R apply(T t) throws E;

}
