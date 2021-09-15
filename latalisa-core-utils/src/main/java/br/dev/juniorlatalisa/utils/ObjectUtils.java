package br.dev.juniorlatalisa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

public final class ObjectUtils {

	private ObjectUtils() {
	}

	public static <T> T encodeObject(Object value, Function<byte[], T> converter) {
		return converter.apply(encodeObject(value));
	}

	public static byte[] encodeObject(Object value) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			try {
				ObjectOutputStream objout = new ObjectOutputStream(buffer);
				try {
					objout.writeObject(value);
				} finally {
					objout.close();
				}
			} finally {
				buffer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> T decodeObject(byte[] value) {
		ByteArrayInputStream buffer = new ByteArrayInputStream(value);
		try {
			try {
				ObjectInputStream objin = new ObjectInputStream(buffer);
				try {
					return (T) objin.readObject();
				} finally {
					objin.close();
				}
			} finally {
				buffer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(OutputStream out, Object... objects) {
		try {
			ObjectOutputStream objout = new ObjectOutputStream(out);
			try {
				for (Object obj : objects) {
					objout.writeObject(obj);
					objout.flush();
				}
			} finally {
				objout.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void read(InputStream in, List<Object> objects) {
		try {
			ObjectInputStream objin = new ObjectInputStream(in);
			try {
				Object obj;
				try {
					while ((obj = objin.readObject()) != null) {
						objects.add(obj);
					}
				} catch (EOFException e) {
					// Sem mais objetos
				}
			} finally {
				objin.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
