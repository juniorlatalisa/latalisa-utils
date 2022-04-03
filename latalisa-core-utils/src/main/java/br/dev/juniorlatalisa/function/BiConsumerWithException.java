package br.dev.juniorlatalisa.function;

/**
 * Permitir exceção à interface BiConsumer
 * 
 * @author Junior Latalisa
 *
 * @param <T> o tipo do primeiro argumento para a operação
 * @param <U> o tipo do segundo argumento para a operação
 * @param <E> o tipo da exceção ao executar o método
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface BiConsumerWithException<T, U, E extends Throwable> {

	/**
	 * Executa esta operação nos argumentos fornecidos e retorna a própria
	 * instância.
	 *
	 * @param t o primeiro argumento de entrada
	 * @param u o segundo argumento de entrada
	 * @see java.util.function.BiConsumer#accept(Object, Object)
	 * @throws E exceção esperada pelo método
	 */
	void accept(T t, U u) throws E;

	/**
	 * Executa esta operação nos argumentos fornecidos e retorna a própria
	 * instância.
	 *
	 * @param t o primeiro argumento de entrada
	 * @param u o segundo argumento de entrada
	 * @return a própria instância.
	 * @see java.util.function.BiConsumer#accept(Object, Object)
	 * @throws E exceção esperada pelo método
	 */
	default BiConsumerWithException<T, U, E> acceptAndThen(T t, U u) throws E {
		accept(t, u);
		return this;
	}

}
