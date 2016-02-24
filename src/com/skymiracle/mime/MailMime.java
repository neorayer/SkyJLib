package com.skymiracle.mime;

import java.util.List;

public class MailMime {

	private String subject;

	private List<RichMailAddress> tos;

	private List<RichMailAddress> ccs;

	private List<RichMailAddress> bccs;

	private String transferEncoding;

	private RichMailAddress reply;

	private RichMailAddress from;

	private StringBuffer htmlContent;

	private StringBuffer plainContent;

	private List<Attachment> downAtts;

	private List<Attachment> innerAtts;

	private ContentType contentType;

	public MailMime() {
		
	}
	public String getTransferEncoding() {
		return transferEncoding;
	}

	public void setTransferEncoding(String transferEncoding) {
		this.transferEncoding = transferEncoding;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public RichMailAddress getReply() {
		return reply;
	}

	public void setReply(RichMailAddress reply) {
		this.reply = reply;
	}

	public RichMailAddress getFrom() {
		return from;
	}

	public void setFrom(RichMailAddress from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<RichMailAddress> getTos() {
		return tos;
	}

	public void setTos(List<RichMailAddress> tos) {
		this.tos = tos;
	}

	public List<RichMailAddress> getCcs() {
		return ccs;
	}

	public void setCcs(List<RichMailAddress> ccs) {
		this.ccs = ccs;
	}

	public List<RichMailAddress> getBccs() {
		return bccs;
	}

	public void setBccs(List<RichMailAddress> bccs) {
		this.bccs = bccs;
	}

	public StringBuffer getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(StringBuffer htmlContent) {
		this.htmlContent = htmlContent;
	}

	public StringBuffer getPlainContent() {
		return plainContent;
	}

	public void setPlainContent(StringBuffer plainContent) {
		this.plainContent = plainContent;
	}

	public List<Attachment> getDownAtts() {
		return downAtts;
	}

	public void setDownAtts(List<Attachment> downAtts) {
		this.downAtts = downAtts;
	}

	public List<Attachment> getInnerAtts() {
		return innerAtts;
	}

	public void setInnerAtts(List<Attachment> innerAtts) {
		this.innerAtts = innerAtts;
	}

}
