package br.dev.juniorlatalisa.function;

/**
 * Adicionar funcionalidades à interface Consumer
 * 
 * @author Junior Latalisa
 *
 * @param <T> o tipo de entrada para a operação
 * 
 * @see java.util.function.Consumer
 */
public interface Consumer<T> extends java.util.function.Consumer<T> {

	/**
	 * Executa esta operação no argumento fornecido.
	 * 
	 * @param t o argumento de entrada
	 * @return a própria instância.
	 * @see java.util.function.Consumer#accept(Object)
	 */
	default Consumer<T> acceptAndThen(T t) {
		accept(t);
		return this;
	}

}
