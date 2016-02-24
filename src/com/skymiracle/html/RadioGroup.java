package com.skymiracle.html;

import java.util.HashMap;
import java.util.Map;

public class RadioGroup extends HtmlTag<RadioGroup> {

	private String name;
	
	private Map<Radio, String> radiosMap = new HashMap<Radio, String>();
	
	public RadioGroup(String name) {
		super("radio", true);
		this.name = name;
	}
	
	public void putRadio(String title, Object value) {
		Radio radio = new Radio(name);
		radio.put("value", value);
		radiosMap.put(radio, title);
		
		add(radio);
		add(new Span(title));
	}


	
	
}
