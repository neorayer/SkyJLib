package com.skymiracle.mime;

public class ContentTypeImpl extends RichAttributeImpl implements ContentType {

	public ContentTypeImpl(String src) {
		super(src);
	}

	public String getType() {
		return getAttrValue("type");
	}

	public String getCharset() {
		String charset =  getAttrValue("charset");
		if (charset.equalsIgnoreCase("gb2312"))
			return "GBK";
		else 
			return charset;
	}

	public String getBoundary() {
		new String();
		return getAttrValue("boundary");
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExtname() {
		String result = ".bin";
		String major = getMajor();
		String second = getSecond();
		if (major.equals("text") && second.equals("html"))
			result = "html";
		else if (major.equals("text") && (second.equals("plain"))) {
			result = "txt";
		}
		return result;
	}

}
