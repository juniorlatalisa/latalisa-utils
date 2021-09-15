package br.dev.juniorlatalisa.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 */
public class CpfCnpjValidator implements ConstraintValidator<CpfCnpjValidator.CpfCnpj, String> {

	private CpfCnpj annotation;

	@Override
	public void initialize(CpfCnpj annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ((((value == null) || (value.isEmpty())) && (!annotation.required())) || (isValid(value)));
	}

	public static boolean isValid(String value) {
		return ((value != null) && ((
				((value = value.replaceAll(Constants.NOT_DIGIT_PATTERN, "")).length() == 11)
					&& (!((value.startsWith("000000000")) || (value.startsWith("111111111"))
						|| (value.startsWith("222222222")) || (value.startsWith("333333333"))
						|| (value.startsWith("444444444")) || (value.startsWith("555555555"))
						|| (value.startsWith("666666666")) || (value.startsWith("777777777"))
						|| (value.startsWith("888888888")) || (value.startsWith("999999999"))))
					&& (value.equals(CPFValidator.calc(value)))
				) || (
				(value.length() == 14)
					&& (!((value.startsWith("000000000000")) || (value.startsWith("111111111111"))
						|| (value.startsWith("222222222222")) || (value.startsWith("333333333333"))
						|| (value.startsWith("444444444444")) || (value.startsWith("555555555555"))
						|| (value.startsWith("666666666666")) || (value.startsWith("777777777777"))
						|| (value.startsWith("888888888888")) || (value.startsWith("999999999999"))))
					&& (value.equals(CNPJValidator.calc(value))))
			));
	}

	@Documented
	@ReportAsSingleViolation
	@Constraint(validatedBy = CpfCnpjValidator.class)
	@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CpfCnpj {

		String message() default "{br.com.virtualsistemas.common.validations.cpfcnpj}";

		boolean required() default false;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}
}