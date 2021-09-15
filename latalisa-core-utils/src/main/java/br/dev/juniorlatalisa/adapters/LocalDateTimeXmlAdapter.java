package br.dev.juniorlatalisa.adapters;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 *
 */
public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {

	@Override
	public LocalDateTime unmarshal(String value) throws Exception {
		return ((value == null) || (value.isEmpty())) ? null
				: LocalDateTime.parse(value, Constants.BRAZIL_DATE_TIME_FORMATTER);
	}

	@Override
	public String marshal(LocalDateTime value) throws Exception {
		return (value == null) ? null : value.format(Constants.BRAZIL_DATE_TIME_FORMATTER);
	}
}