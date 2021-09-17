package br.dev.juniorlatalisa.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void dateToAnyDateTime() {
		final Date agora = new Date();
		Assert.assertEquals(DateUtils.dateToLocalDateTime(agora),
				DateUtils.dateToAnyDateTime(agora, LocalDateTime::ofInstant));
	}

	@Test
	public void zonedDateTime() {
		Assert.assertNotNull(DateUtils.dateToAnyDateTime(new Date(), ZonedDateTime::ofInstant));
	}

	@Test
	public void dateDiff() {
		final Date startDate = DateUtils.dateFrom(LocalDateTime.of(1979, 9, 7, 10, 15));
		final Date endDate = DateUtils.dateFrom(LocalDateTime.of(2021, 9, 7, 10, 15));
		Assert.assertEquals(DateUtils.dateDiff(startDate, endDate, ChronoUnit.DAYS), 15341);
	}

}
