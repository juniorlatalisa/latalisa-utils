package br.dev.juniorlatalisa.builders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MimeMessageBuilder implements Builder<MimeMessage> {

	/**
	 * @param session
	 * @see MimeMessage#MimeMessage(Session)
	 */
	public MimeMessageBuilder(Session session) {
		System.setProperty("mail.mime.encodefilename", "true");
		try {
			(this.message = new MimeMessage(session)).setContent(this.content = new MimeMultipart());
			this.charset = "UTF-8";
			setDispositionAttachment();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private MimeMessage message;
	private MimeMultipart content;
	private String disposition;
	private String charset;

	@Override
	public MimeMessage build() {
		return message;
	}

	protected String getCharset() {
		return charset;
	}

	protected String getDisposition() {
		return disposition;
	}

	protected Function<DataHandler, String> getAttachmentIDReolver() {
		return attachmentIDReolver;
	}

	/**
	 * charset padrão
	 * 
	 * @param charset
	 * @return
	 */
	public MimeMessageBuilder setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 * @see MimeMessage#addHeader(String, String)
	 */
	public MimeMessageBuilder addHeader(String name, String value) {
		try {
			message.addHeader(name, value);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param address
	 * @return
	 * @see MimeMessage#setFrom(String)
	 */
	public MimeMessageBuilder setFrom(String address) {
		try {
			message.setFrom(address);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param address
	 * @return
	 * @see InternetAddress#InternetAddress(String)
	 * @throws AddressException
	 */
	protected Address createAddress(String address) throws AddressException {
		return new InternetAddress(address);
	}

	/**
	 * @param address
	 * @param personal
	 * @return
	 * @see InternetAddress#InternetAddress(String, String, String)
	 * @throws UnsupportedEncodingException
	 */
	protected Address createAddress(String address, String personal) throws UnsupportedEncodingException {
		return new InternetAddress(address, personal, getCharset());
	}

	/**
	 * @param address
	 * @param personal
	 * @return
	 * @see MimeMessage#setFrom(Address)
	 * @see InternetAddress#InternetAddress(String, String, String)
	 */
	public MimeMessageBuilder setFrom(String address, String personal) {
		try {
			message.setFrom(createAddress(address, personal));
		} catch (UnsupportedEncodingException | MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param address
	 * @return
	 * @see MimeMessage#setFrom(Address)
	 */
	public MimeMessageBuilder setFrom(Address address) {
		try {
			message.setFrom(address);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * Adiciona o header Disposition-Notification-To para o endereço informado
	 * 
	 * @param address
	 * @return
	 */
	public MimeMessageBuilder setConfirmation(String address) {
		try {
			message.setHeader("Disposition-Notification-To", address);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param addresses
	 * @return
	 * @see MimeMessage#setReplyTo(Address[])
	 */
	public MimeMessageBuilder setReplyTo(Address... addresses) {
		try {
			message.setReplyTo(addresses);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param address
	 * @return
	 * @see MimeMessage#setReplyTo(Address[])
	 * @see InternetAddress#InternetAddress(String)
	 */
	public MimeMessageBuilder setReplyTo(String address) {
		try {
			message.setReplyTo(new Address[] { createAddress(address) });
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param address
	 * @param personal
	 * @return
	 * @see MimeMessage#setReplyTo(Address[])
	 * @see InternetAddress#InternetAddress(String, String, String)
	 */
	public MimeMessageBuilder setReplyTo(String address, String personal) {
		try {
			message.setReplyTo(new Address[] { createAddress(address, personal) });
		} catch (UnsupportedEncodingException | MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param subject
	 * @return
	 * @see MimeMessage#setSubject(String, String)
	 */
	public MimeMessageBuilder setSubject(String subject) {
		try {
			message.setSubject(subject, getCharset());
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param type
	 * @param address
	 * @return
	 * @see MimeMessage#addRecipients(RecipientType, String)
	 */
	public MimeMessageBuilder addRecipient(RecipientType type, String address) {
		try {
			message.addRecipients(type, address);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param type
	 * @param address
	 * @param personal
	 * @return
	 * @see MimeMessage#addRecipient(RecipientType, Address)
	 * @see InternetAddress#InternetAddress(String, String, String)
	 */
	public MimeMessageBuilder addRecipient(RecipientType type, String address, String personal) {
		try {
			message.addRecipient(type, createAddress(address, personal));
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param type
	 * @param addresses
	 * @return
	 * @see MimeMessage#addRecipients(RecipientType, Address[])
	 */
	public MimeMessageBuilder addRecipient(RecipientType type, Address... addresses) {
		try {
			message.addRecipients(type, addresses);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param text
	 * @param charset
	 * @param subtype
	 * @return
	 * @see MimeMultipart#addBodyPart(javax.mail.BodyPart)
	 * @see MimeBodyPart#MimeBodyPart()
	 */
	public MimeMessageBuilder addBody(String text, String charset, String subtype) {
		try {
			MimeBodyPart body = new MimeBodyPart();
			body.setText(text, charset, subtype);
			content.addBodyPart(body);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param text
	 * @param subtype
	 * @return
	 * @see MimeMessageBuilder#addBody(String, String, String)
	 */
	public MimeMessageBuilder addBody(String text, String subtype) {
		return addBody(text, getCharset(), subtype);
	}

	/**
	 * @param value
	 * @return
	 * @see MimeMessageBuilder#addBody(String, String, String)
	 */
	public MimeMessageBuilder addBodyText(String value) {
		return addBody(value, getCharset(), "plain");
	}

	/**
	 * @param value
	 * @return
	 * @see MimeMessageBuilder#addBody(String, String, String)
	 */
	public MimeMessageBuilder addBodyHtml(String value) {
		return addBody(value, getCharset(), "html");
	}

	/**
	 * attachment
	 * 
	 * @return
	 * @see MimeBodyPart#setDisposition(String)
	 */
	public MimeMessageBuilder setDispositionAttachment() {
		this.disposition = "attachment";
		return this;
	}

	/**
	 * inline
	 * 
	 * @return
	 * @see MimeBodyPart#setDisposition(String)
	 */
	public MimeMessageBuilder setDispositionInline() {
		this.disposition = "inline";
		return this;
	}

	private static final Function<DataHandler, String> CONTENT_ID_RESOLVER = DataHandler::getName;

	private Function<DataHandler, String> attachmentIDReolver = CONTENT_ID_RESOLVER;

	/**
	 * @param reolver
	 * @return
	 * @see MimeBodyPart#setContentID(String)
	 */
	public MimeMessageBuilder setAttachmentID(Function<DataHandler, String> reolver) {
		this.attachmentIDReolver = Optional.ofNullable(reolver).orElse(CONTENT_ID_RESOLVER);
		return this;
	}

	/**
	 * @param dataHandler
	 * @return
	 * @see MimeMultipart#addBodyPart(javax.mail.BodyPart)
	 */
	public MimeMessageBuilder addAttachment(DataHandler dataHandler) {
		try {
			MimeBodyPart attachment = new MimeBodyPart();
			attachment.setDataHandler(dataHandler);
			attachment.setDisposition(getDisposition());
			attachment.setFileName(dataHandler.getName());
			attachment.setContentID(getAttachmentIDReolver().apply(dataHandler));
			content.addBodyPart(attachment);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * @param dataSource
	 * @return
	 * @see DataHandler#DataHandler(DataSource)
	 */
	public MimeMessageBuilder addAttachment(DataSource dataSource) {
		return addAttachment(new DataHandler(dataSource));
	}

	/**
	 * @param url
	 * @return
	 * @see DataHandler#DataHandler(URL)
	 */
	public MimeMessageBuilder addAttachment(URL url) {
		return addAttachment(new DataHandler(url));
	}

	/**
	 * @param file
	 * @return
	 * @see DataHandler#DataHandler(Object, String)
	 */
	public MimeMessageBuilder addAttachment(File file) {
		return addAttachment(new DataHandler(new FileDataSource(file)));
	}

	/**
	 * @param data
	 * @param type
	 * @param name
	 * @return
	 * @see ByteArrayDataSource#ByteArrayDataSource(byte[], String)
	 * @see DataHandler#DataHandler(DataSource)
	 */
	public MimeMessageBuilder addAttachment(byte[] data, String type, String name) {
		ByteArrayDataSource dataSource = new ByteArrayDataSource(data, type);
		dataSource.setName(name);
		return addAttachment(new DataHandler(dataSource));
	}

	/**
	 * @param is
	 * @param type
	 * @param name
	 * @return
	 * @see ByteArrayDataSource#ByteArrayDataSource(InputStream, String)
	 */
	public MimeMessageBuilder addAttachment(InputStream is, String type, String name) {
		try {
			ByteArrayDataSource dataSource = new ByteArrayDataSource(is, type);
			dataSource.setName(name);
			return addAttachment(new DataHandler(dataSource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}