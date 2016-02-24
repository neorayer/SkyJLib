package com.skymiracle.html;

import java.util.HashMap;
import java.util.Map;

public class Select extends FormElTag<Select> {

	private Map<String, String> optionsMap = new HashMap<String, String>();

	public Select(String name) {
		super("select", null, name, true);
	}

	public void putOption(Object value, String title) {
		String sVal = String.valueOf(value);
		optionsMap.put(sVal, title);
		add(new Option(sVal, title));
	}

	private class Option extends HtmlTag<Option> {

		public Option(String value, String title) {
			super("option", true);
			put("value", value);
			setContent(title);
		}

	}

}
