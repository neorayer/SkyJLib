package com.skymiracle.ldap;

public class AttributeSchema {

	private String name;

	private String value;

	public AttributeSchema(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
