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
 * @see <a href="https://www.geradorcnpj.com/algoritmo_do_cnpj.htm">Decifrando o
 *      Algoritmo do CNPJ</a>
 */
public class CNPJValidator implements ConstraintValidator<CNPJValidator.CNPJ, String> {

	private CNPJ annotation;

	@Override
	public void initialize(CNPJ annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ((((value == null) || (value.isEmpty())) && (!annotation.required())) || (isValid(value)));
	}

	public static boolean isValid(String value) {
		return ((!((value == null) || ((value = value.replaceAll(Constants.NOT_DIGIT_PATTERN, "")).length() != 14)
				|| (value.startsWith("000000000000")) || (value.startsWith("111111111111"))
				|| (value.startsWith("222222222222")) || (value.startsWith("333333333333"))
				|| (value.startsWith("444444444444")) || (value.startsWith("555555555555"))
				|| (value.startsWith("666666666666")) || (value.startsWith("777777777777"))
				|| (value.startsWith("888888888888")) || (value.startsWith("999999999999"))))
				&& (value.equals(calc(value))));
	}

	public static String calc(String value) {
		final char[] entrada = value.toCharArray();
		final char[] retorno = new char[14];
		int[] multiplicacao = { 0, 0 };

		int mod, vchar;

		int[] mult = { 5, 6 };
		for (int i = 0; i < 12; i++) {
			vchar = Integer.parseInt(String.valueOf(retorno[i] = entrada[i]), 10);
			multiplicacao[0] += vchar * mult[0];
			multiplicacao[1] += vchar * mult[1];

			mult[0] = (mult[0] == 2) ? 9 : mult[0] - 1;
			mult[1] = (mult[1] == 2) ? 9 : mult[1] - 1;
		}

		mod = multiplicacao[0] % 11;
		mod = (mod < 2) ? 0 : 11 - mod;

		retorno[12] = Integer.toString(mod).charAt(0);

		multiplicacao[1] += (mod * 2);

		mod = multiplicacao[1] % 11;
		retorno[13] = Integer.toString((mod < 2) ? 0 : 11 - mod).charAt(0);

		return new String(retorno);
	}

	@Documented
	@ReportAsSingleViolation
	@Constraint(validatedBy = CNPJValidator.class)
	@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CNPJ {

		String message() default "{br.com.virtualsistemas.common.validations.cnpj}";

		boolean required() default false;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}
}