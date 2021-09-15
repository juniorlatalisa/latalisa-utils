package br.dev.juniorlatalisa.builders;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class MapBuilderTest {

	@Test
	public void build() {
		Assert.assertTrue(new MapBuilder<String, String>().build(Map::isEmpty));
	}

}
