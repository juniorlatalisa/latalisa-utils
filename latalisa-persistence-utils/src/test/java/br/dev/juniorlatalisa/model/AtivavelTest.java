package br.dev.juniorlatalisa.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AtivavelTest {

	@Test
	public void comparator() {
		List<Ativavel> letras = Arrays.asList(() -> Boolean.TRUE, () -> Boolean.TRUE, () -> Boolean.FALSE, () -> null,
				null, () -> Boolean.TRUE);
		letras.sort(Ativavel.COMPARATOR_POR_ATIVO);
		Object[] ordenado = letras.stream().map(l -> (l == null) ? null : l.getAtivo()).toArray();
		System.out.println(Arrays.toString(ordenado));
		Assert.assertEquals(Boolean.FALSE, ordenado[0]);
	}

}
