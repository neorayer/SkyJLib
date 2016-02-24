package com.skymiracle.html;

public  class Span extends HtmlTag<Span> {

	public Span() {
		super("span", true);
	}

	public Span(String content) {
		super("span", true);
		setContent(content);
	}

	
	
}
