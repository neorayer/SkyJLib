package com.skymiracle.ldap;

import java.util.ArrayList;

public class ObjectClassSchema {

	private String name;

	private ArrayList reqAttrList = new ArrayList();

	private ArrayList optAttrList = new ArrayList();

	private String description;

	private String value;

	private int type;

	public ObjectClassSchema(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getOptAttrList() {
		return this.optAttrList;
	}

	public void setOptAttrList(ArrayList optAttrList) {
		this.optAttrList = optAttrList;
	}

	public ArrayList getReqAttrList() {
		return this.reqAttrList;
	}

	public void setReqAttrList(ArrayList reqAttrList) {
		this.reqAttrList = reqAttrList;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public String getTypeStr() {
		if (this.type == 1)
			return "STRUCTURAL";
		if (this.type == 2)
			return "AUXILIARY";
		return "" + this.type;
	}

}
