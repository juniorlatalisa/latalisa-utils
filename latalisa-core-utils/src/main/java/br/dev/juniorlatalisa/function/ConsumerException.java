package br.dev.juniorlatalisa.function;

/**
 * Permitir exceção à interface Consumer
 * 
 * @author Junior Latalisa
 *
 * @param <T> o tipo de entrada para a operação
 * @param <E> o tipo da exceção ao executar o método
 * 
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ConsumerException<T, E extends Throwable> {

	/**
	 * Executa esta operação no argumento fornecido.
	 * 
	 * @param t o argumento de entrada
	 * @see java.util.function.Consumer#accept(Object)
	 * @throws E exceção esperada pelo método
	 */
	void accept(T t) throws E;

	/**
	 * Executa esta operação no argumento fornecido.
	 * 
	 * @param t o argumento de entrada
	 * @return a própria instância.
	 * @see java.util.function.Consumer#accept(Object)
	 * @throws E exceção esperada pelo método
	 */
	default ConsumerException<T, E> acceptAndThen(T t) throws E {
		accept(t);
		return this;
	}

}
