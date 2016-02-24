package com.skymiracle.html;

public abstract class FormElTag<T extends FormElTag<T>> extends HtmlTag<T> {

	public FormElTag(String tagName, String type, String name, boolean hasInner) {
		super(tagName, hasInner);
		put("type", type);
		put("name", name);
	}

	public FormElTag(String type, String name, boolean hasInner) {
		this("input",  type,  name, hasInner);
	}


	
	
}
