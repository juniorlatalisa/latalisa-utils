package br.dev.juniorlatalisa.validations;

import org.junit.Assert;
import org.junit.Test;

public class CNPJValidatorTest {

	@Test
	public void isValid() {
		Assert.assertTrue(CNPJValidator.isValid("11.444.777/0001-61"));
	}

	@Test
	public void isInvalid() {
		Assert.assertFalse(CNPJValidator.isValid("11.444.777/0001-62"));
	}

}