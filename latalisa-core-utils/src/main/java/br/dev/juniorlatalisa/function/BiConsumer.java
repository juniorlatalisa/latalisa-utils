package br.dev.juniorlatalisa.function;

/**
 * Adicionar funcionalidades à interface BiConsumer
 * 
 * @author Junior Latalisa
 *
 * @param <T> o tipo do primeiro argumento para a operação
 * @param <U> o tipo do segundo argumento para a operação
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface BiConsumer<T, U> extends java.util.function.BiConsumer<T, U> {

	/**
	 * Executa esta operação nos argumentos fornecidos e retorna a própria
	 * instância.
	 *
	 * @param t o primeiro argumento de entrada
	 * @param u o segundo argumento de entrada
	 * @return a própria instância.
	 * @see java.util.function.BiConsumer#accept(Object, Object)
	 */
	default BiConsumer<T, U> acceptAndThen(T t, U u) {
		accept(t, u);
		return this;
	}

}
