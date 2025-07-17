package br.dev.juniorlatalisa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ObjectUtils {

	private ObjectUtils() {
		throw new IllegalArgumentException();
	}

	public static <T> T encodeObject(Object value, Function<byte[], T> converter) {
		return converter.apply(encodeObject(value));
	}

	private static void encodeObject(ByteArrayOutputStream buffer, Object value) throws IOException {
		try (ObjectOutputStream objout = new ObjectOutputStream(buffer)) {
			objout.writeObject(value);
		}
	}

	public static byte[] encodeObject(Object value) {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			encodeObject(buffer, value);
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object decodeObject(ByteArrayInputStream buffer) throws IOException, ClassNotFoundException {
		try (ObjectInputStream objin = new ObjectInputStream(buffer)) {
			return objin.readObject();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T decodeObject(byte[] value) {
		try (ByteArrayInputStream buffer = new ByteArrayInputStream(value)) {
			return (T) decodeObject(buffer);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(OutputStream out, Object... objects) {
		try (ObjectOutputStream objout = new ObjectOutputStream(out)) {
			for (Object obj : objects) {
				objout.writeObject(obj);
				objout.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void read(ObjectInputStream objin, List<Object> objects) throws ClassNotFoundException, IOException {
		try {
			Object obj;
			while ((obj = objin.readObject()) != null) {
				objects.add(obj);
			}
		} catch (EOFException e) {
			// Sem mais objetos
		}
	}

	public static void read(InputStream in, List<Object> objects) {
		try (ObjectInputStream objin = new ObjectInputStream(in)) {
			read(objin, objects);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> boolean isEmpty(T[] value) {
		return value == null || value.length == 0;
	}

	public static boolean isEmpty(CharSequence value) {
		return value == null || value.length() == 0;
	}

	public static boolean isEmpty(Collection<?> value) {
		return value == null || value.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> value) {
		return value == null || value.isEmpty();
	}

	public static boolean isEmpty(Number value) {
		return value == null || value.doubleValue() == 0.0;
	}

	@SafeVarargs
	public static boolean isAnyEmpty(CharSequence... values) {
		for (CharSequence value : values) {
			if (isEmpty(value)) {
				return true;
			}
		}
		return false;
	}

	@SafeVarargs
	public static boolean isAllEmpty(CharSequence... values) {
		for (CharSequence value : values) {
			if (!isEmpty(value)) {
				return false;
			}
		}
		return true;
	}

	@SafeVarargs
	public static <T> boolean isAnyEquals(T base, T... values) {
		if (base == null) {
			return false;
		}
		for (T value : values) {
			if (value == null) {
				continue;
			}
			if (base.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@SafeVarargs
	public static <T> boolean isAllEquals(T base, T... values) {
		if (base == null) {
			return false;
		}
		for (T value : values) {
			if (value == null || !base.equals(value)) {
				return false;
			}
		}
		return true;
	}

	public static Throwable getCause(Throwable throwable) {
		if (throwable.getCause() == null || throwable.equals(throwable.getCause())) {
			return throwable.getCause() == null ? throwable : throwable.getCause();
		}
		return getCause(throwable.getCause());
	}

	public static LocalDateTime now() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public static LocalDateTime today() {
		return LocalDate.now().atStartOfDay();
	}
}
