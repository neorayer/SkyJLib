package com.skymiracle.html;

public  class Tr extends HtmlTag<Tr> {

	public Tr() {
		super("tr", true);
	}

	public Td addTd() {
		return add(new Td());
	}
	public Th addTh() {
		return add(new Th());
	}

	
}
