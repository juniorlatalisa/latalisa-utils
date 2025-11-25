package br.dev.juniorlatalisa.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import br.dev.juniorlatalisa.Constants;

/**
 * Simplificar acesso aos metos relacionados a arquivos
 * 
 * @author juniorlatalisa
 */
public final class FileUtils {

	private FileUtils() {
	}

	/**
	 * @see File#lastModified()
	 */
	public static Date getLastModified(File file) {
		long lastModified;
		return ((lastModified = file.lastModified()) > 0) ? new Date(lastModified) : null;
	}

	/**
	 * @see URLConnection#getContentType()
	 * @see IOException
	 */
	public static String getContentType(URL file) {
		try {
			return file.openConnection().getContentType();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @see #getContentType(URL)
	 * @see MalformedURLException
	 */
	public static String getContentType(File file) {
		try {
			return getContentType(file.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @see Paths#get(String, String...)
	 * @see Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)
	 * @see IOException
	 */
	public static Path createDirectories(String root, String... more) {
		try {
			return Files.createDirectories(Paths.get(root, more));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Garante que o diretório especificado exista, criando-o caso necessário.
	 * <p>
	 * Esta versão recebe um diretório raiz e segmentos adicionais de caminho,
	 * construindo um {@link Path} e delegando para
	 * {@link #createDirectoriesIfNeeded(Path)}.
	 * </p>
	 *
	 * @param root diretório raiz a partir do qual o caminho será construído
	 * @param more segmentos adicionais do caminho (subdiretórios)
	 * @return o {@link Path} representando o diretório existente ou recém-criado
	 * @throws UncheckedIOException se ocorrer um erro de I/O ao criar os diretórios
	 */
	@SafeVarargs
	public static Path createDirectoriesIfNeeded(String root, String... more) {
		return createDirectoriesIfNeeded(Paths.get(root, more));
	}

	/**
	 * Garante que o diretório especificado exista, criando-o caso necessário.
	 * <p>
	 * Se o caminho já existir, ele é retornado diretamente. Caso contrário, os
	 * diretórios necessários são criados utilizando
	 * {@link Files#createDirectories(Path)}.
	 * </p>
	 *
	 * <p>
	 * Qualquer {@link IOException} gerada durante a criação dos diretórios é
	 * encapsulada em uma {@link UncheckedIOException}, permitindo que o método seja
	 * usado sem necessidade de tratamento explícito de exceções checadas.
	 * </p>
	 *
	 * @param path caminho do diretório a ser verificado/criado
	 * @return o {@link Path} representando o diretório existente ou recém-criado
	 * @throws UncheckedIOException se ocorrer um erro de I/O ao criar os diretórios
	 *
	 * @see java.nio.file.Files#createDirectories(Path)
	 * @see java.nio.file.Files#exists(Path)
	 */
	public static Path createDirectoriesIfNeeded(Path path) {
		try {
			return Files.exists(path) ? path : Files.createDirectories(path);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static File createDirectories(File root, String... more) {
		File retorno = root;
		for (String dir : more) {
			retorno = new File(retorno, dir);
		}
		retorno.mkdirs();
		return retorno;
	}

	/**
	 * @see Path#resolve(String)
	 * @see Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)
	 */
	public static Path createDirectories(Path root, String... more) {
		Path retorno = root;
		for (String dir : more) {
			retorno = retorno.resolve(dir);
		}
		try {
			return Files.createDirectories(retorno);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @see Files#readAllBytes(Path)
	 * @see IOException
	 */
	public static byte[] read(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static byte[] read(File file) {
		return read(file.toPath());
	}

	public static byte[] read(URL url) {
		try {
			return read(url.openStream(), 512);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static byte[] read(InputStream is) {
		return read(is, -1);
	}

	public static byte[] read(InputStream is, int bufferSize) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			try {
				byte[] buffer = new byte[(bufferSize > 0) ? bufferSize : is.available()];
				int n;
				while ((n = is.read(buffer)) > 0) {
					out.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return out.toByteArray();
	}

	/**
	 * @see Files#write(Path, byte[], OpenOption...)
	 * @see IOException
	 */
	public static void write(Path path, byte[] bytes, OpenOption... options) {
		try {
			Files.write(path, bytes, options);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void write(File file, byte[] bytes, OpenOption... options) {
		write(file.toPath(), bytes, options);
	}

	public static String checksum(byte[] value, String algorithm) {
		return StringUtils.encodeHEX(CryptoUtils.encrypt(value, algorithm));
	}

	public static String checksum(byte[] value) {
		return checksum(value, Constants.SHA256_ALGORITHM);
	}

	public static String checksum(Path path) {
		return checksum(read(path), Constants.SHA256_ALGORITHM);
	}

	public static String checksum(File file) {
		return checksum(read(file), Constants.SHA256_ALGORITHM);
	}

	public static String checksum(URL url) {
		return checksum(read(url), Constants.SHA256_ALGORITHM);
	}

	public static String checksum(InputStream is) {
		return checksum(read(is, -1), Constants.SHA256_ALGORITHM);
	}
}