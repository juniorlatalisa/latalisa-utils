package br.dev.juniorlatalisa.utils;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

	@Test
	public void encodedecode() {
		LocalDateTime agora = LocalDateTime.now();
		byte[] buffer = ObjectUtils.encodeObject(agora);
		LocalDateTime value = ObjectUtils.decodeObject(buffer);
		Assert.assertEquals(agora, value);
	}

}
