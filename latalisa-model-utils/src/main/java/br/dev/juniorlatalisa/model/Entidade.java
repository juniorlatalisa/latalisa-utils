package br.dev.juniorlatalisa.model;

import java.io.Serializable;

public interface Entidade extends Serializable {

	String CURRENCY_COLUMN_DEFINITION = "NUMERIC(18,2)";
	
	String SELECT_FROM = "select e from ";
	String SELECT_WHERE = " e where ";

}