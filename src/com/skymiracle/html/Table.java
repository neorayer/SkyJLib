package com.skymiracle.html;

public  class Table extends HtmlTag<Table> {

	public Table() {
		super("table", true);
	}

	public Tr addTr() {
		return add(new Tr());
	}
	
}
