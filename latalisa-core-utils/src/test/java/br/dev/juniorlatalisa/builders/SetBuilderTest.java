package br.dev.juniorlatalisa.builders;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SetBuilderTest {

	@Test
	public void build() {
		Assert.assertTrue(new SetBuilder<String>().build(Set::isEmpty));
	}

}
