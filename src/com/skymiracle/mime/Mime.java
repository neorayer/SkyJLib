package com.skymiracle.mime;

import java.util.List;

import com.skymiracle.util.LinesSection;

public interface Mime {
	public MimeHeader getHeader();

	public RichMailAddress getFrom();

	public List<RichMailAddress> getTo();

	public List<RichMailAddress> getCc();

	public List<RichMailAddress>getBcc();

	public String getReplyMail();

	public String getSubject();

	public String getDate();

	public String getContentTextHtml();

	public String getContentTextPlain();

	public List<Attachment> getDownableAttachments();

	public List<Attachment> getInnerAttachments();

	public ContentType getContentType();

	public String getBodyStr();

	public String save(String dirPath) throws Exception;

	public LinesSection getBodyLs();

	public String getHeaderStr();

	public String getContentTransferEncoding();

	public ContentDisposition getContentDisposition();

	public String getAttrValue(String name);
}
