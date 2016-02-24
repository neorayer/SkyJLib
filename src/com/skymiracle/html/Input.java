package com.skymiracle.html;

public abstract class Input<T extends Input<T>> extends FormElTag<T> {

	public Input(String type, String name) {
		super("input", name, false);
		put("type", type);
	}


	
	
}
