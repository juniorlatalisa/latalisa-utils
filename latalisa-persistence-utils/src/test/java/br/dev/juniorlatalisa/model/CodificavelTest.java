package br.dev.juniorlatalisa.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CodificavelTest {

	@Test
	public void comparatorInteger() {
		List<Codificavel<Integer>> letras = Arrays.asList(() -> 16, () -> 132, () -> 10, () -> null, null, () -> 200);
		letras.sort(Codificavel.COMPARATOR_POR_CODIGO);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getCodigo()).toArray();
		Assert.assertEquals(10, ordenado[2]);
	}

	@Test
	public void comparatorDouble() {
		List<Codificavel<Double>> letras = Arrays.asList(() -> 16.0, () -> 132.0, () -> 10.0, () -> null, null,
				() -> 200.66);
		letras.sort(Codificavel.COMPARATOR_POR_CODIGO);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getCodigo()).toArray();
		Assert.assertEquals(10.0, ordenado[2]);
	}
}