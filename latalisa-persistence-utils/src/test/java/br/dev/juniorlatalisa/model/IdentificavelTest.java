package br.dev.juniorlatalisa.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IdentificavelTest {

	@Test
	public void comparatorString() {
		List<Identificavel<String>> letras = Arrays.asList(() -> "z", () -> "b", () -> "a", () -> null, null,
				() -> "y");
		letras.sort(Identificavel.COMPARATOR_POR_IDENTIFICADOR);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getIdentificador()).toArray();
		Assert.assertEquals("a", ordenado[2]);
	}

	@Test
	public void comparatorInteger() {
		List<Identificavel<Integer>> letras = Arrays.asList(() -> 16, () -> 132, () -> 10, () -> null, null, () -> 200);
		letras.sort(Identificavel.COMPARATOR_POR_IDENTIFICADOR);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getIdentificador()).toArray();
		Assert.assertEquals(10, ordenado[2]);
	}

}