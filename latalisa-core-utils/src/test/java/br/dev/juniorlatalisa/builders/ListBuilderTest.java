package br.dev.juniorlatalisa.builders;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ListBuilderTest {

	@Test
	public void build() {
		Assert.assertTrue(new ListBuilder<String>().build(List::isEmpty));
	}

	@Test
	public void lazy() {
		boolean[] validation = { false };
		List<String> lazy = ListBuilder.lazy(() -> lazy(validation, "A", "B"));
		System.out.println(lazy.getClass() + ": " + validation[0]);
		if (validation[0]) {
			Assert.fail();
			return;
		}
		System.out.println(lazy.size() + ": " + validation[0]);
		Assert.assertTrue(validation[0]);
	}

	@SafeVarargs
	public static <T> T[] lazy(boolean[] validation, T... elemens) {
		validation[0] = true;
		return elemens;
	}

}
