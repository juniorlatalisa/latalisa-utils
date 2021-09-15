package br.dev.juniorlatalisa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @author juniorlatalisa
 */
public final class MavenUtils {

	private MavenUtils() {
	}

	public static Optional<String> getVersion(ClassLoader loader, String groupId, String artifactId) {
		return Optional.ofNullable(getProperties(loader, groupId, artifactId).getProperty("version", null));
	}

	public static Optional<String> getVersion(Class<?> packageClass, String groupId, String artifactId) {
		return getVersion(packageClass.getClassLoader(), groupId, artifactId);
	}

	public static Properties getProperties(Class<?> packageClass, String groupId, String artifactId) {
		return getProperties(packageClass.getClassLoader(), groupId, artifactId);
	}

	public static Properties getProperties(ClassLoader loader, String groupId, String artifactId) {
		Properties retorno = new Properties();
		String key = new StringBuilder().append("/META-INF/maven/").append(groupId).append('/').append(artifactId)
				.append("/pom.properties").toString();
		InputStream is = loader.getResourceAsStream(key);
		if (is != null) {
			try {
				try {
					retorno.load(is);
				} finally {
					is.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return retorno;
	}
}