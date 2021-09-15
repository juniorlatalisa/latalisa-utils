package br.dev.juniorlatalisa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 *
 */
public final class CryptoUtils {

	private CryptoUtils() {
	}

	public static byte[] encrypt(byte[] value, Key key, AlgorithmParameterSpec params, String transformation,
			int opmode) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			if (params == null) {
				cipher.init(opmode, key);
			} else {
				cipher.init(opmode, key, params);
			}
			return cipher.doFinal(value);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(byte[] value, Key key, String transformation, int opmode) {
		return encrypt(value, key, null, transformation, opmode);
	}

	public static byte[] encrypt(byte[] value, String algorithm) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			try {
				messageDigest.update(value);
				return messageDigest.digest();
			} finally {
				messageDigest.reset();
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(byte[] value) {
		return encrypt(value, Constants.SHA256_ALGORITHM);
	}

	public static byte[] encrypt(byte[] value, byte[] key) {
		return encrypt(value, new SecretKeySpec(encrypt(key, Constants.MD5_ALGORITHM), Constants.AES_ALGORITHM), null,
				Constants.AES_ALGORITHM, Cipher.ENCRYPT_MODE);
	}

	public static byte[] encrypt(String value, String key) {
		return encrypt(value.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] decrypt(byte[] value, byte[] key) {
		return encrypt(value, new SecretKeySpec(encrypt(key, Constants.MD5_ALGORITHM), Constants.AES_ALGORITHM), null,
				Constants.AES_ALGORITHM, Cipher.DECRYPT_MODE);
	}

	public static String decrypt(byte[] value, String key) {
		return new String(decrypt(value, key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

	public static byte[] encrypt(byte[] value, Key key, int opmode) {
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
			cipher.init(opmode, key);
			return cipher.doFinal(value);
		} catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(byte[] value, PublicKey publicKey) {
		return encrypt(value, publicKey, Cipher.ENCRYPT_MODE);
	}

	public static byte[] encrypt(String value, PublicKey publicKey) {
		return encrypt(value.getBytes(StandardCharsets.UTF_8), publicKey, Cipher.ENCRYPT_MODE);
	}

	public static byte[] decrypt(byte[] value, PrivateKey privateKey) {
		return encrypt(value, privateKey, Cipher.DECRYPT_MODE);
	}

	public static KeyPair generateKeyPair() {
		return generateKeyPair(-1);
	}

	public static KeyPair generateKeyPair(int keysize) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Constants.RSA_ALGORITHM);
			if (keysize > 0) {
				keyPairGenerator.initialize(keysize);
			}
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static void generateKeyPair(OutputStream privateKey, OutputStream publicKey) {
		generateKeyPair(privateKey, publicKey, -1);
	}

	public static void generateKeyPair(OutputStream privateKey, OutputStream publicKey, int keysize) {
		try {
			KeyPair keyPair = generateKeyPair(keysize);
			privateKey.write(keyPair.getPrivate().getEncoded());
			privateKey.flush();
			publicKey.write(keyPair.getPublic().getEncoded());
			publicKey.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(byte[] privateKey, byte[] publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(Constants.RSA_ALGORITHM);
			return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(publicKey)),
					keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey)));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(InputStream privateKey, InputStream publicKey) {
		try {
			byte[] publicKeyBuffer, privateKeyBuffer;
			privateKey.read(privateKeyBuffer = new byte[privateKey.available()]);
			publicKey.read(publicKeyBuffer = new byte[publicKey.available()]);
			return generateKeyPair(privateKeyBuffer, publicKeyBuffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static PublicKey generatePublicKey(byte[] publicKeyBuffer) {
		try {
			return KeyFactory.getInstance(Constants.RSA_ALGORITHM)
					.generatePublic(new X509EncodedKeySpec(publicKeyBuffer));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static PublicKey generatePublicKey(InputStream publicKey) {
		try {
			byte[] publicKeyBuffer;
			publicKey.read(publicKeyBuffer = new byte[publicKey.available()]);
			return generatePublicKey(publicKeyBuffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static PrivateKey generatePrivateKey(byte[] privateKeyBuffer) {
		try {
			return KeyFactory.getInstance(Constants.RSA_ALGORITHM)
					.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBuffer));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static PrivateKey generatePrivateKey(InputStream privateKey) {
		try {
			byte[] privateKeyBuffer;
			privateKey.read(privateKeyBuffer = new byte[privateKey.available()]);
			return generatePrivateKey(privateKeyBuffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}