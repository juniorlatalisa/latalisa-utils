package br.dev.juniorlatalisa.model;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Facilitar a cópia dos valores entre instâncias.
 * 
 * @author juniorlatalisa
 *
 * @param <T> A própria class que implementa a interface
 */
@FunctionalInterface
public interface Copiavel<T> extends Serializable {

	/**
	 * Copiar os valores de outra instância para essa.
	 * 
	 * @param origem Instância que se pretende copiar os valores
	 */
	void copiar(T origem);

	/**
	 * Copia os valores dessa instância para outra objtida através do gerador
	 * 
	 * @param <C>     Tipo de instância produzida pelo gerador
	 * @param gerador Exemplo: <code>Copiavel::new</code>
	 * @return Outra instância com os valores copiados desta.
	 */
	@SuppressWarnings("unchecked")
	default <C extends Copiavel<T>> C copiar(Supplier<C> gerador) {
		C destino = gerador.get();
		destino.copiar((T) this);
		return destino;
	}
}