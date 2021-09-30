package br.dev.juniorlatalisa.rest.control;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Priority;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import br.dev.juniorlatalisa.Constants;

@Provider
@EncryptionNeeded
@Priority(EncryptionImpl.PRIORITY)
public class EncryptionImpl implements ReaderInterceptor, WriterInterceptor {

	public static final String PUBLIC_KEY = "PublicKey";
	public static final String PRIVATE_KEY = "PrivateKey";
	public static final int PRIORITY = Priorities.USER + 1000;

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		PublicKey pk = (PublicKey) context.getProperty(PUBLIC_KEY);
		if (pk == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pk);
			CipherOutputStream out = new CipherOutputStream(context.getOutputStream(), cipher);
			try {
				context.proceed();
			} finally {
				out.close();
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			throw new WebApplicationException(e);
		}
	}

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		PrivateKey pk = (PrivateKey) context.getProperty(PRIVATE_KEY);
		if (pk == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, pk);
			CipherInputStream in = new CipherInputStream(context.getInputStream(), cipher);
			try {
				return context.proceed();
			} finally {
				in.close();
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			throw new WebApplicationException(e);
		}
	}
}
