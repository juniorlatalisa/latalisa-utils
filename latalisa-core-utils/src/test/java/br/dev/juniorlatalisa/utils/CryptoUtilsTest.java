package br.dev.juniorlatalisa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class CryptoUtilsTest {

	public static final String LINHA = "___________________________";

	@Test
	public void generateKeyPair() {
		String key1 = StringUtils.encodeBase64(CryptoUtils.generateKeyPair().getPublic().getEncoded());
		String key2 = StringUtils.encodeBase64(CryptoUtils.generateKeyPair().getPublic().getEncoded());
		Assert.assertNotEquals(key1, key2);
	}

	@Test
	public void sha256() {
		final String teste = "Teste CryptoUtils SHA-256";
		final byte[] encrypted = CryptoUtils.encrypt(teste.getBytes(StandardCharsets.UTF_8));
		System.out.append("\n\n").append(LINHA).append("\n").append("Teste SHA-256 ")
				.append(CryptoUtils.class.getSimpleName()).append("\n")
				.append(new String(encrypted, StandardCharsets.UTF_8)).append("\n").append(LINHA).append("\n\n");
		Assert.assertTrue(Arrays.equals(encrypted, CryptoUtils.encrypt(teste.getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	public void aes() {
		final String KEY = "12345678";
		final String TESTE = new Date().toString();
		byte[] aux = CryptoUtils.encrypt(TESTE, KEY);
		System.out.append("\n\n").append(LINHA).append("\n").append("Teste AES ")
				.append(CryptoUtils.class.getSimpleName()).append("\n").append(TESTE).append("\n")
				.append(new String(aux)).append("\n").append(LINHA).append("\n\n");
		Assert.assertTrue(TESTE.equals(CryptoUtils.decrypt(aux, KEY)));
	}

	@Test
	public void rsa() {
		try {
			final File tempDir = new File(System.getProperty("java.io.tmpdir"));
			final File privateKey = new File(tempDir, "CryptoUtilsTestPrivate.key");
			final File publicKey = new File(tempDir, "CryptoUtilsTestPublic.key");
			if (!((privateKey.exists()) && (publicKey.exists()))) {
				OutputStream privateKeyOut, publicKeyOut;
				CryptoUtils.generateKeyPair(privateKeyOut = new FileOutputStream(privateKey),
						publicKeyOut = new FileOutputStream(publicKey));
				privateKeyOut.close();
				publicKeyOut.close();
			}
			InputStream privateKeyIn, publicKeyIn;
			final KeyPair keyPair = CryptoUtils.generateKeyPair(privateKeyIn = new FileInputStream(privateKey),
					publicKeyIn = new FileInputStream(publicKey));
			privateKeyIn.close();
			publicKeyIn.close();
			Assert.assertTrue(rsa(keyPair.getPrivate(), keyPair.getPublic()));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected boolean rsa(PrivateKey privateKey, PublicKey publicKey) {
		final String teste = "Teste CryptoUtils RSA";
		final byte[] encrypted = CryptoUtils.encrypt(teste, publicKey);
		final byte[] decrypeted = CryptoUtils.decrypt(encrypted, privateKey);
		String retorno;
		System.out.append("\n\n").append(LINHA).append("\n").append("Teste RSA ")
				.append(CryptoUtils.class.getSimpleName()).append("\n")
				.append(new String(encrypted, StandardCharsets.UTF_8)).append("\n").append(privateKey.toString())
				.append("\n").append(publicKey.toString()).append("\n")
				.append(retorno = new String(decrypeted, StandardCharsets.UTF_8)).append("\n").append(LINHA)
				.append("\n\n");
		return retorno.equals(teste);
	}
}