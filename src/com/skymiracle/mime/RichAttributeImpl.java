package com.skymiracle.mime;

import java.util.Hashtable;

public class RichAttributeImpl implements RichAttribute {

	private String src;

	Hashtable ht;

	private String major;

	private String second;

	public RichAttributeImpl(String src) {
		this.src = src;
		this.ht = new Hashtable();
		if (this.src != null)
			parse();
	}

	private void parse() {
		String[] s = this.src.split(";");

		{
			int pos = s[0].indexOf('/');
			if (pos < 0) {
				this.major = s[0];
				this.second = "";
			} else {
				this.major = s[0].substring(0, pos).trim();
				this.second = s[0].substring(pos + 1).trim();
			}
		}
		for (int i = 1; i < s.length; i++) {
			int pos = s[i].indexOf('=');
			if (pos < 0)
				continue;
			String name = s[i].substring(0, pos).trim();
			String value = s[i].substring(pos + 1).trim();
			if (value.startsWith("\""))
				value = value.substring(1);
			if (value.endsWith("\""))
				value = value.substring(0, value.length() - 1);
			this.ht.put(name.toLowerCase(), value);
		}
	}

	public String getAttrValue(String attrName) {
		Object value = this.ht.get(attrName.toLowerCase());
		if (value == null)
			return "";
		else
			return (String) value;
	}

	public String getMajor() {
		if (this.major == null)
			return "";
		return this.major;
	}

	public String getSecond() {
		if (this.second == null)
			return "";
		return this.second;
	}

	public String getSrc() {
		return this.src;
	}

}
