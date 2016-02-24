package com.skymiracle.html;

public  class Div extends HtmlTag<Div> {

	public Div() {
		super("div", true);
	}

	public Div(String content) {
		this();
		setContent(content);
	}

	
	
}
