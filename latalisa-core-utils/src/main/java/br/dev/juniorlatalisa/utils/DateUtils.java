package br.dev.juniorlatalisa.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.function.BiFunction;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 *
 */
public final class DateUtils {

	private DateUtils() {
		throw new IllegalArgumentException();
	}

	public static <T> T dateToAnyDateTime(Date date, BiFunction<Instant, ZoneId, T> function) {
		return function.apply(date.toInstant(), Constants.BRAZIL_ZONE_OFFSET);
	}

	public static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), Constants.BRAZIL_ZONE_OFFSET);
	}

	public static LocalDate dateToLocalDate(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), Constants.BRAZIL_ZONE_OFFSET).toLocalDate();
	}

	public static LocalTime dateToLocalTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), Constants.BRAZIL_ZONE_OFFSET).toLocalTime();
	}

	public static Date dateFrom(ChronoLocalDateTime<?> value) {
		return Date.from(value.toInstant(Constants.BRAZIL_ZONE_OFFSET));
	}

	public static Date dateFrom(LocalDate value) {
		return Date.from(value.atStartOfDay(Constants.BRAZIL_ZONE_OFFSET).toInstant());
	}

	public static Date dateFrom(LocalTime value) {
		return Date.from(value.atDate(LocalDate.now()).toInstant(Constants.BRAZIL_ZONE_OFFSET));
	}

	public static long dateDiff(Date startDate, Date endDate, TemporalUnit unit) {
		return dateToLocalDateTime(startDate).until(dateToLocalDateTime(endDate), unit);
	}
}
