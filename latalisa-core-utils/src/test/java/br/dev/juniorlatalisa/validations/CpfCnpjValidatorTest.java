package br.dev.juniorlatalisa.validations;

import org.junit.Assert;
import org.junit.Test;

public class CpfCnpjValidatorTest {

	@Test
	public void isValidCPF() {
		Assert.assertTrue(CpfCnpjValidator.isValid("111.444.777-35"));
	}

	@Test
	public void isValidCNPJ() {
		Assert.assertTrue(CpfCnpjValidator.isValid("11.444.777/0001-61"));
	}
}