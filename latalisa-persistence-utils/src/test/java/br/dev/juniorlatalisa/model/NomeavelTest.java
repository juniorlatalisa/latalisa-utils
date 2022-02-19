package br.dev.juniorlatalisa.model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class NomeavelTest {

	private static final List<Nomeavel> letras = Arrays.asList(() -> "z", () -> "b", () -> "a", () -> null, null,
			() -> "Y");

	public static <T extends Nomeavel> T preencher(T entidade) {
		entidade.setNome(UUID.randomUUID().toString());
		return entidade;
	}

	@Test
	public void comparator() {
		Object[] ordenado = letras.stream().sorted(Nomeavel.COMPARATOR_POR_NOME)
				.map(l -> (l == null) ? null : l.getNome()).toArray();
		Assert.assertEquals("Y", ordenado[2]);
	}

	@Test
	public void comparatorIgnoreCase() {
		Object[] ordenado = letras.stream().sorted(Nomeavel.COMPARATOR_POR_NOME_IGNORE_CASE)
				.map(l -> (l == null) ? null : l.getNome()).toArray();
		Assert.assertEquals("a", ordenado[2]);
	}

}
