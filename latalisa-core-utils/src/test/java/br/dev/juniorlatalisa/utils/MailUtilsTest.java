package br.dev.juniorlatalisa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.dev.juniorlatalisa.utils.MailUtils.MailSessionData;

public class MailUtilsTest {

	private static MailSessionData data = null;

	@BeforeClass
	public static void beforeClass() {
		MailUtils.setDebug(true);
		InputStream is = MailUtilsTest.class.getResourceAsStream("/META-INF/MailUtilsTest.properties");
		if (is == null) {
			return;
		}
		try {
			Properties properties = new Properties();
			try {
				properties.load(is);
			} finally {
				is.close();
			}
			if (properties.containsKey("host")) {
				(data = new MailSessionData())//
						.setHost(properties.getProperty("host"))//
						.setUser(properties.getProperty("user"))//
						.setPassword(properties.getProperty("password"))//
						.setPort(properties.getProperty("port"))//
						.setAddress(properties.getProperty("address"))//
						.setProtocol(properties.getProperty("protocol"))//
						.setStartTLS(Boolean.parseBoolean(properties.getProperty("tls")));
			} else {
				data = null;
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Test
	public void send() {
		if (data != null) {
			MimeMessage message = MailUtils.//
					createMimeMessageBuilder(data)//
					.addHeader("virtual-teste", "header-test")//
					// .addHeader("Disposition-Notification-To", PROPS.getProperty("user"))//
					.setFrom(data.getAddress(), "Origem do teste \uD83D\uDE00")//
					.setConfirmation(data.getAddress())//
					.setSubject("\uD83C\uDF55".concat(LocalDateTime.now().toString()))//
					.addRecipient(RecipientType.TO, "paraojuniorler@gmail.com", "Destino do teste \uD83D\uDE0E")//
					.addBodyHtml("Texto HTML de teste do <strong>body</strong> &#128545; \uD83D\uDE21")//
					.addAttachment(MailUtilsTest.class.getResourceAsStream("/META-INF/MailUtilsTest.properties"),
							MediaType.TEXT_PLAIN, "teste.txt")//
					.build();
			MailUtils.send(message, data);
		}
	}

	@Test
	public void authentication() {
		if (data != null) {
			Assert.assertTrue(MailUtils.authentication(data));
		}
	}

//	@Test
//	public void authenticationFail() {
//		if (data != null) {
//			Assert.assertFalse(MailUtils.authentication(data));
//		}
//	}
}