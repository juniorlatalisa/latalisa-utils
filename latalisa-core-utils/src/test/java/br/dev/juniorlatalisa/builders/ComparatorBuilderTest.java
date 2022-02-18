package br.dev.juniorlatalisa.builders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTextField;

import org.junit.Assert;
import org.junit.Test;

public class ComparatorBuilderTest {

	@Test
	public void compareList() {
		var list = List.of(7, 8, 1);
		list = list.stream().sorted(ComparatorBuilder.build(Integer::compareTo)).collect(Collectors.toList());
		Assert.assertEquals(1, list.get(0).intValue());
	}

	@Test
	public void compareClass() {
		var list = List.of(new JTextField("Titulo 10", 10), new JTextField("Titulo 8", 8),
				new JTextField("Titulo 11", 10), new JTextField("Titulo 2", 2));
		list = list.stream().sorted(ComparatorBuilder.builder(JTextField::getColumns).add(JTextField::getText).build())
				.collect(Collectors.toList());
		Assert.assertEquals("Titulo 10", list.get(2).getText());
	}

	@Test
	public void compareListNull() {
		var list = Arrays.asList(7, null, 8, 1, null);
		list = list.stream().sorted(ComparatorBuilder.build(Integer::compareTo)).collect(Collectors.toList());
		Assert.assertEquals(8, list.get(list.size() - 1).intValue());
	}

}
