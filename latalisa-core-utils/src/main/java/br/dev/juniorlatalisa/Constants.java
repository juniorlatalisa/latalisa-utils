package br.dev.juniorlatalisa;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author juniorlatalisa
 */
public interface Constants {

	Locale BRAZIL = new Locale("pt", "BR");

	ZoneOffset BRAZIL_ZONE_OFFSET = LocalDateTime.now().atZone(ZoneId.of("America/Sao_Paulo")).getOffset();

	TimeZone BRAZIL_TIME_ZONE = TimeZone.getTimeZone("America/Sao_Paulo");

	String BRAZIL_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

	String BRAZIL_DATE_PATTERN = "dd/MM/yyyy";

	String BRAZIL_TIME_PATTERN = "HH:mm:ss";

	DateTimeFormatter BRAZIL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(BRAZIL_DATE_TIME_PATTERN, BRAZIL);

	DateTimeFormatter BRAZIL_DATE_FORMATTER = DateTimeFormatter.ofPattern(BRAZIL_DATE_PATTERN, BRAZIL);

	DateTimeFormatter BRAZIL_TIME_FORMATTER = DateTimeFormatter.ofPattern(BRAZIL_TIME_PATTERN, BRAZIL);

	SimpleDateFormat BRAZIL_DATE_TIME_FORMAT = new SimpleDateFormat(BRAZIL_DATE_TIME_PATTERN, BRAZIL);

	SimpleDateFormat BRAZIL_DATE_FORMAT = new SimpleDateFormat(BRAZIL_DATE_PATTERN, BRAZIL);

	SimpleDateFormat BRAZIL_TIME_FORMAT = new SimpleDateFormat(BRAZIL_TIME_PATTERN, BRAZIL);

	DecimalFormatSymbols BRAZIL_DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance(BRAZIL);

	String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";

	String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)+";

	String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

	String EMAIL_PATTERN = "^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$";

	/**
	 * @see <a href='http://tools.ietf.org/html/rfc1123'>rfc1123</a>
	 */
	String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss Z";

	String NOT_DIGIT_PATTERN = "\\D";

	String DIGIT_PATTERN = "\\d";

	String ONE_OR_MORE_DIGITS_PATTERN = ".*\\d+.*";

	String DIGIT_ONLY_PATTERN = "\\d+";

	String AES_ALGORITHM = "AES";

	String RSA_ALGORITHM = "RSA";

	String SHA256_ALGORITHM = "SHA-256";

	String MD5_ALGORITHM = "MD5";

	/**
	 * For our passwords we are going to implement a strict policy about their
	 * format. We want our passwords to :
	 * <ul>
	 * <li>Be between 8 and 40 characters long</li>
	 * <li>Contain at least one digit.</li>
	 * <li>Contain at least one lower case character.</li>
	 * <li>Contain at least one upper case character.</li>
	 * <li>Contain at least on special character from [ @ # $ % ! . ].</li>
	 * </ul>
	 * 
	 * @see <a href=
	 *      "https://examples.javacodegeeks.com/core-java/util/regex/matcher/validate-password-with-java-regular-expression-example">Validate
	 *      Password with Java Regular Expression example</a>
	 */
	String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";

}
