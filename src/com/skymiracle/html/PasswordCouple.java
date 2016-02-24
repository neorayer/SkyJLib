package com.skymiracle.html;

public class PasswordCouple extends HtmlTag<PasswordCouple> {
	public PasswordCouple(String name) {
		super("span", true);
		
		add(new Password(name));
		addBr();
		add( new Password(name+"2"));
	}

	
}
