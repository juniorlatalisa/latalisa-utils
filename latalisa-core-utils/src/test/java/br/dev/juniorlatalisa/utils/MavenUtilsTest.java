package br.dev.juniorlatalisa.utils;

import javax.enterprise.context.ApplicationScoped;

import org.junit.Assert;
import org.junit.Test;

public class MavenUtilsTest {

	private static final Class<?> packageClass = ApplicationScoped.class;
	private static final String groupId = "org.glassfish.main";
	private static final String artifactId = "javaee-api";
	private static final String notfound = "Não enconrado";

	@Test
	public void version() {
		Assert.assertNotEquals(MavenUtils.getVersion(packageClass, groupId, artifactId) //
				.orElse(notfound), notfound);
	}

	public static void main(String[] args) {
		System.out.println(MavenUtils.getVersion(packageClass, groupId, artifactId));
	}
}