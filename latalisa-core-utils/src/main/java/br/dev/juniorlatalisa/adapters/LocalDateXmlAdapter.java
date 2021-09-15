package br.dev.juniorlatalisa.adapters;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 *
 */
public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public LocalDate unmarshal(String value) throws Exception {
		return ((value == null) || (value.isEmpty())) ? null : LocalDate.parse(value, Constants.BRAZIL_DATE_FORMATTER);
	}

	@Override
	public String marshal(LocalDate value) throws Exception {
		return (value == null) ? null : value.format(Constants.BRAZIL_DATE_FORMATTER);
	}
}