package br.dev.juniorlatalisa.adapters;

import java.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.dev.juniorlatalisa.Constants;

/**
 * @author juniorlatalisa
 *
 */
public class LocalTimeXmlAdapter extends XmlAdapter<String, LocalTime> {

	@Override
	public LocalTime unmarshal(String value) throws Exception {
		return ((value == null) || (value.isEmpty())) ? null : LocalTime.parse(value, Constants.BRAZIL_TIME_FORMATTER);
	}

	@Override
	public String marshal(LocalTime value) throws Exception {
		return (value == null) ? null : value.format(Constants.BRAZIL_TIME_FORMATTER);
	}
}