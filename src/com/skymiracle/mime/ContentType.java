package com.skymiracle.mime;

public interface ContentType extends RichAttribute {
	public String getMajor();

	public String getSecond();

	public String getType();

	public String getCharset();

	public String getBoundary();

	public String getName();

	public String getSrc();

	public String getExtname();
}
