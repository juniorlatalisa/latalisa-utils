package br.dev.juniorlatalisa.utils;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {

	private static final byte[] TOKEN = "76fdf8d8-da1d-4834-a126-1478e4436a14".getBytes(StandardCharsets.UTF_8);

	@Test
	public void checksum() {
		Assert.assertEquals("f805df66feac86214d03dc93f4fac98592b29a0b16fb542a8d3bfd33e8787b3c",
				FileUtils.checksum(TOKEN));
	}

}