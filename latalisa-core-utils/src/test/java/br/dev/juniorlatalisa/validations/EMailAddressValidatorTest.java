package br.dev.juniorlatalisa.validations;

import org.junit.Assert;
import org.junit.Test;

public class EMailAddressValidatorTest {

	@Test
	public void comNome() {
		Assert.assertTrue(EmailAddressValidator.isValid("José <jose@teste.com>"));
	}

	@Test
	public void semNome() {
		Assert.assertTrue(EmailAddressValidator.isValid("jose@teste.com"));
	}

	@Test
	public void isInvalid() {
		Assert.assertFalse(EmailAddressValidator.isValid("joseteste.com"));
	}

}
