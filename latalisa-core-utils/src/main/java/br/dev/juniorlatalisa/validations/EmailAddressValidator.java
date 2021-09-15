package br.dev.juniorlatalisa.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

/**
 * Usa a classe {@link InternetAddress} para validar o e-mail
 * 
 * @author juniorlatalisa
 * @see InternetAddress
 * @see InternetAddress#InternetAddress(String, boolean)
 */
public class EmailAddressValidator implements ConstraintValidator<EmailAddressValidator.EmailAddress, String> {

	private EmailAddress annotation;

	@Override
	public void initialize(EmailAddress annotation) {
		this.annotation = annotation;
	}

	public static boolean isValid(String value) {
		InternetAddress address;
		try {
			address = new InternetAddress(value, true);
		} catch (AddressException e) {
			return false;
		}
		return (!(address.getAddress() == null || address.getAddress().isEmpty()));
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ((((value == null) || (value.isEmpty())) && (!annotation.required())) || (isValid(value)));
	}

	@Documented
	@ReportAsSingleViolation
	@Constraint(validatedBy = EmailAddressValidator.class)
	@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface EmailAddress {

		String message() default "{br.com.virtualsistemas.common.validations.email.address}";

		boolean required() default false;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

	}

}
