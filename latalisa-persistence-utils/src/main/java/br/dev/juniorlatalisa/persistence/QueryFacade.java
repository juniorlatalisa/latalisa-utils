package br.dev.juniorlatalisa.persistence;

public interface QueryFacade {

	/**
	 * Quando um mecanismo de banco de dados não aceitar esse caracter no script em
	 * UTF-8 essa constante irá auxíliar para retira-lo.
	 * 
	 * @see <a href="https://pt.wikipedia.org/wiki/Marca_de_ordem_de_byte">Marca de
	 *      ordem de byte</a>
	 */
	static final String UTF8_BOM = "\uFEFF";

	/**
	 * Constante para padronizar que o inicio do resultado é de forma padrão do
	 * mecanismo de banco de dados.
	 */
	static final int START_RESULT_NONE = -1;

	/**
	 * Constante para padronizar que os registros devem ser retornados de forma
	 * padrão do mecanismo de banco de dados (sem limites).
	 */
	static final int MAX_RESULT_NONE = -1;

	/**
	 * Constante para sinalizar que o número de linhas afetadas pelo script não pode
	 * ser aferida.
	 */
	static final int EXECUTE_RESULT_NONE = -1;

}
