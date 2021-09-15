package br.dev.juniorlatalisa.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IdentificavelTest {

	@Test
	public void comparator() {
		List<Identificavel<String>> letras = Arrays.asList(() -> "z", () -> "b", () -> "a", () -> null, null,
				() -> "y");
		letras.sort(Identificavel.COMPARATOR_POR_IDENTIFICADOR);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getIdentificador()).toArray();
		// System.out.println(Arrays.toString(ordenado));
		Assert.assertEquals("a", ordenado[0]);
	}

}
