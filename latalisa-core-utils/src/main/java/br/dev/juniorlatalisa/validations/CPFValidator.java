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
 * @see <a href="https://www.geradorcpf.com/algoritmo_do_cpf.htm">Decifrando o
 *      Algoritmo do CPF</a>
 */
public class CPFValidator implements ConstraintValidator<CPFValidator.CPF, String> {

	private CPF annotation;

	@Override
	public void initialize(CPF annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ((((value == null) || (value.isEmpty())) && (!annotation.required())) || (isValid(value)));
	}

	public static boolean isValid(String value) {
		return ((!((value == null) || ((value = value.replaceAll(Constants.NOT_DIGIT_PATTERN, "")).length() != 11)
				|| (value.startsWith("000000000")) || (value.startsWith("111111111")) || (value.startsWith("222222222"))
				|| (value.startsWith("333333333")) || (value.startsWith("444444444")) || (value.startsWith("555555555"))
				|| (value.startsWith("666666666")) || (value.startsWith("777777777")) || (value.startsWith("888888888"))
				|| (value.startsWith("999999999")))) && (value.equals(calc(value))));
	}

	public static String calc(String value) {
		final char[] entrada = value.toCharArray();
		final char[] retorno = new char[11];
		int[] multiplicacao = { 0, 0 };

		int mod, mult, vchar;

		mult = 10;
		for (int i = 0; i < 9; i++) {
			vchar = Integer.parseInt(String.valueOf(retorno[i] = entrada[i]), 10);
			multiplicacao[0] += vchar * mult;
			multiplicacao[1] += vchar * (1 + mult--);
		}

		mod = multiplicacao[0] % 11;
		mult = (mod < 2) ? 0 : 11 - mod;

		retorno[9] = Integer.toString(mult).charAt(0);

		multiplicacao[1] += (mult * 2);

		mod = multiplicacao[1] % 11;
		retorno[10] = Integer.toString((mod < 2) ? 0 : 11 - mod).charAt(0);

		return new String(retorno);
	}

	@Documented
	@ReportAsSingleViolation
	@Constraint(validatedBy = CPFValidator.class)
	@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CPF {

		String message() default "{br.com.virtualsistemas.common.validations.cpf}";

		boolean required() default false;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}
}