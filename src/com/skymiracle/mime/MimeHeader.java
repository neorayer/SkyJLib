package com.skymiracle.mime;

import java.util.Date;
import java.util.List;

/**
 * mail head message
 * 
 * @author Administrator
 * 
 */
public interface MimeHeader {

	public boolean isMultipart();

	public ContentType getContentType();

	public ContentDisposition getContentDisposition();

	public String getContentLocation();

	public String getContentTransferEncoding();

	public RichMailAddress getFrom();

	public List<RichMailAddress> getTo();

	public List<RichMailAddress> getCc();

	public List<RichMailAddress> getBcc();

	public String getDate();
	
	public String getLocalTime();
	
	public Date getLocalDate();
	
	public String getSubject();

	public String getContentId();

	public String getAttrValue(String attrName);

	public void addAttrValue(String name, String value);

	public String getPriority();
}
