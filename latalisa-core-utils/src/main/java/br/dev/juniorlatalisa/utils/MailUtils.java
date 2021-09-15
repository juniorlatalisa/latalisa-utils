package br.dev.juniorlatalisa.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;

import br.dev.juniorlatalisa.builders.MimeMessageBuilder;

public final class MailUtils {

	private static final Logger LOGGER = Logger.getLogger("MailUtils");
	private static final PrintStream DEBUG_OUT = new PrintStream(new OutputStream() {

		private final StringBuilder buffer = new StringBuilder();
		private final String sourceClass = MailUtils.class.getName();

		@Override
		public void write(int b) throws IOException {
			buffer.append((char) b);
		}

		@Override
		public void flush() throws IOException {
			String log;
			synchronized (buffer) {
				log = buffer.toString();
				buffer.setLength(0);
			}
			if (!(log == null || log.isEmpty() || log.equals("\r\n"))) {
				LOGGER.logp(Level.INFO, sourceClass, "debug", log);
			}
		};
	}, true);

	private static boolean debug = false;
	private static PrintStream debugOut = null;

	private static Function<MessagingException, Boolean> handleMessagingException = exception -> false;

	public static void setDebug(boolean debug) {
		MailUtils.debug = debug;
	}

	public void setDebugOut(PrintStream debugOut) {
		MailUtils.debugOut = debugOut;
	}

	public static void setHandleMessagingException(Function<MessagingException, Boolean> handleMessagingException) {
		Objects.requireNonNull(handleMessagingException);
		MailUtils.handleMessagingException = handleMessagingException;
	}

	public static Authenticator getAuthenticator(String user, String password) {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		};
	}

	public static boolean send(Message message) {
		try {
			Transport.send(message);
			return true;
		} catch (MessagingException e) {
			return handleMessagingException.apply(e);
		}
	}

	public static boolean send(Message message, MailSessionData data) {
		try {
			message.saveChanges();// do this first
			Transport transport = message.getSession().getTransport(data.getProtocol());
			transport.connect(data.getUser(), data.getPassword());
			try {
				transport.sendMessage(message, message.getAllRecipients());
			} finally {
				transport.close();
			}
			return true;
		} catch (MessagingException e) {
			return handleMessagingException.apply(e);
		}
	}

	/**
	 * Validar autenticação via SMTP Google:
	 * https://myaccount.google.com/lesssecureapps
	 */
	public static boolean authentication(MailSessionData data) {
		try {
			Service service = getSession(data).getTransport(data.getProtocol());
			service.connect(data.getUser(), data.getPassword());
			try {
				return service.isConnected();
			} finally {
				service.close();
			}
		} catch (MessagingException e) {
			if (!(e instanceof AuthenticationFailedException)) {
				LOGGER.warning(e.getMessage());
			}
			return handleMessagingException.apply(e);
		}
	}

	protected static void setSession(Properties properties, String protocol, String key, Object data) {
		String value;
		if (data == null || (value = (data instanceof String) ? (String) data : data.toString()).isEmpty()) {
			return;
		}
		properties.setProperty(protocol.concat(key), value);
	}

	public static Session getSession(MailSessionData data) {
		final Properties properties = (data.getProperties() == null || data.getProperties().isEmpty())//
				? new Properties()
				: new Properties(data.getProperties());

		final String protocol = "mail.".concat((data.getProtocol() == null || data.getProtocol().isEmpty()) //
				? "smtp"
				: data.getProtocol());

		setSession(properties, protocol, ".host", data.getHost());
		setSession(properties, protocol, ".port", data.getPort().toString());
		setSession(properties, protocol, ".ssl.trust", data.getHost());
		setSession(properties, protocol, ".auth", data.getAuth());
		setSession(properties, protocol, ".connectiontimeout", data.getConnectionTimeout());
		setSession(properties, protocol, ".starttls.enable", data.getStartTLS());
		setSession(properties, protocol, ".user", data.getUser());

		return getSession(properties, data.getUser(), data.getPassword());
	}

	public static Session getSession(Properties properties, String user, String password) {
		Session session = Session.getInstance(properties, getAuthenticator(user, password));
		session.setDebugOut((MailUtils.debugOut == null) ? MailUtils.DEBUG_OUT : MailUtils.debugOut);
		session.setDebug(MailUtils.debug);
		return session;
	}

	public static MimeMessageBuilder createMimeMessageBuilder(MailSessionData data) {
		return new MimeMessageBuilder(getSession(data));
	}

	final public static class MailSessionData implements Serializable {

		private static final long serialVersionUID = -7396924913588888480L;

		private String host;
		private String user;
		private String password;
		private String address;
		private Properties properties;

		private Integer port = 465;
		private String protocol = "smtp";
		private Boolean auth = Boolean.TRUE;
		private Boolean startTLS = Boolean.TRUE;
		private Integer connectionTimeout = 5000;

		public String getAddress() {
			return address;
		}

		public MailSessionData setAddress(String address) {
			this.address = address;
			return this;
		}

		public Boolean getStartTLS() {
			return startTLS;
		}

		public MailSessionData setStartTLS(Boolean startTLS) {
			this.startTLS = startTLS;
			return this;
		}

		public Integer getConnectionTimeout() {
			return connectionTimeout;
		}

		public MailSessionData setConnectionTimeout(Integer connecTiontimeout) {
			this.connectionTimeout = connecTiontimeout;
			return this;
		}

		public Boolean getAuth() {
			return auth;
		}

		public MailSessionData setAuth(Boolean auth) {
			this.auth = auth;
			return this;
		}

		public String getProtocol() {
			return protocol;
		}

		public MailSessionData setProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public String getHost() {
			return host;
		}

		public MailSessionData setHost(String host) {
			this.host = host;
			return this;
		}

		public Integer getPort() {
			return port;
		}

		public MailSessionData setPort(Integer port) {
			this.port = port;
			return this;
		}

		public MailSessionData setPort(String port) {
			return this.setPort(Integer.parseInt(port, 10));
		}

		public String getUser() {
			return user;
		}

		public MailSessionData setUser(String user) {
			this.user = user;
			return this;
		}

		public String getPassword() {
			return password;
		}

		public MailSessionData setPassword(String password) {
			this.password = password;
			return this;
		}

		public Properties getProperties() {
			return properties;
		}

		public MailSessionData setProperties(Properties properties) {
			this.properties = properties;
			return this;
		}

		@Override
		public int hashCode() {
			return Objects.hash(address, auth, connectionTimeout, host, password, port, properties, protocol, startTLS,
					user);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MailSessionData other = (MailSessionData) obj;
			return Objects.equals(address, other.address) && Objects.equals(auth, other.auth)
					&& Objects.equals(connectionTimeout, other.connectionTimeout) && Objects.equals(host, other.host)
					&& Objects.equals(password, other.password) && Objects.equals(port, other.port)
					&& Objects.equals(properties, other.properties) && Objects.equals(protocol, other.protocol)
					&& Objects.equals(startTLS, other.startTLS) && Objects.equals(user, other.user);
		}

		@Override
		public String toString() {
			return "MailSessionData [" + (host != null ? "host=" + host + ", " : "")
					+ (user != null ? "user=" + user + ", " : "")
					+ (password != null ? "password=" + password.replaceAll(".", "*") + ", " : "")
					+ (address != null ? "address=" + address + ", " : "")
					+ (properties != null ? "properties=" + properties + ", " : "")
					+ (port != null ? "port=" + port + ", " : "")
					+ (protocol != null ? "protocol=" + protocol + ", " : "")
					+ (auth != null ? "auth=" + auth + ", " : "")
					+ (startTLS != null ? "startTLS=" + startTLS + ", " : "")
					+ (connectionTimeout != null ? "connectionTimeout=" + connectionTimeout : "") + "]";
		}
	}
}