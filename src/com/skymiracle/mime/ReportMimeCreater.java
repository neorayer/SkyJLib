package com.skymiracle.mime;

/**
 * @author Administrator
 * @version 1.0
 * 
 * create report fit by rfc1892
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.skymiracle.io.TextFile;
import com.skymiracle.util.Base64;
import com.skymiracle.util.CalendarUtil;
import com.skymiracle.util.UUID;

@SuppressWarnings("deprecation")
public class ReportMimeCreater  {
	public final static String REPORT_TYPE_DELVIER = "delivery-status";
	public final static String CONTENT_TYPE = "multipart/report";
	public final static String REPORT_BODY_3_CONTENT_TYPE_MAIL = "message/rfc822";
	public final static String REPORT_BODY_3_CONTENT_TYPE_HEADER = "text/rfc822-headers";
	public static final String MIME_VERSION = "1.0";
	public static final String CONTENT_ENCODING = "Base64";
	public static final int MESSAGE_TYPE_ALL = 10;
	public static final int MESSAGE_TYPE_HEADER = 20;
	public static final String ACTION_FAILED = "failed";
	public static final String STATUS_FAILURE = "5.0.0";

	private String messageID;

	private String from;

	private Calendar calendar;

	private String to;

	private String subject;

	private String mainBoundary;

	private String reportType;

	private String content;

	private String original_to;

	private String action;

	private String status;

	private String reason;

	private int orginalMessageType;

	private StringBuffer message;

	private StringBuffer messageHeader;

	private StringBuffer source;

	public ReportMimeCreater(String from, String to, String original_to,
			String subject) {
		this.from = from;
		this.to = to;
		this.original_to = original_to;
		this.subject = subject;
		this.messageID = new UUID().toString();
		this.calendar = Calendar.getInstance();
		this.mainBoundary = "_" + new UUID().toString();
		this.reportType = ReportMimeCreater.REPORT_TYPE_DELVIER;
		this.action = ReportMimeCreater.ACTION_FAILED;
		this.status = ReportMimeCreater.STATUS_FAILURE;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setMessage(int type, StringBuffer message) {
		this.orginalMessageType = type;
		if (this.orginalMessageType == ReportMimeCreater.MESSAGE_TYPE_HEADER)
			this.messageHeader = message;
		else
			this.message = message;
	}

	private StringBuffer createReport(String charset) throws Exception {

		this.source = new StringBuffer(1024);
		// create header
		this.source.append("Mime-Version: ").append(MimeCreater.MIME_VERSION)
				.append("\r\n");
		this.source.append("Message-ID: <").append(this.messageID).append(
				this.from).append(">\r\n");
		this.source.append("Date: ").append(
				CalendarUtil.getDateRFC822(this.calendar)).append("\r\n");
		this.source.append("From: ").append(this.from).append("\r\n");
		this.source.append("To: ").append(this.to).append("\r\n");
		String subject = this.subject.length() == 0 ? "" : "=?"+charset+"?B?"
				+ new String(Base64.encode(this.subject.getBytes(charset))) + "?=";
		this.source.append("Subject: ").append(subject).append("\r\n");
		this.source.append("Content-Type: ").append(
				ReportMimeCreater.CONTENT_TYPE).append(";\r\n\treport-type=")
				.append(this.reportType).append(";\r\n\tboundary=\"").append(
						this.mainBoundary).append("\"\r\n");
		this.source.append("X-Mailer: Skymiracle Wdpost X\r\n");
		this.source.append("\r\n");

		// create body 1:text
		this.source.append("--").append(this.mainBoundary).append("\r\n");
		this.source.append("Content-Type: text/plain;charset=\"").append(charset).append("\"\r\n");
		this.source.append("Content-Transfer-Encoding: ").append(
				ReportMimeCreater.CONTENT_ENCODING).append("\r\n");
		this.source.append("Content-Description: Notification\r\n");
		this.source.append("\r\n").append(
				new String(Base64.encodeFormatted(this.content.getBytes(charset), 0,
						76))).append("\r\n");

		// create body 2:deliver-status
		this.source.append("--").append(this.mainBoundary).append("\r\n");
		this.source.append("Content-Type: ").append("Message/").append(
				this.reportType).append("\r\n");
		this.source.append("Content-Description: Delivery error report\r\n");
		this.source.append("\r\n");
		this.source.append("Final-Recipient: rfc822;").append(this.original_to)
				.append("\r\n");
		this.source.append("Action: ").append(this.action).append("\r\n");
		this.source.append("Status: ").append(this.status).append("\r\n");
		this.source.append("Diagnostic-Code: Skymiracle Wdpost x;").append(
				this.reason).append("\r\n");
		this.source.append("\r\n");

		// create body 3:original message
//		this.source.append("--").append(this.mainBoundary).append("\r\n");
//		if (this.orginalMessageType == ReportMimeCreater.MESSAGE_TYPE_HEADER) {
//			this.source.append("Content-Type: Text/Rfc822-headers\r\n");
//			this.source
//					.append("Content-Description: Undelivered Message Headers\r\n");
//			this.source.append("\r\n");
//			this.source.append(this.messageHeader);
//		} else {
//			this.source.append("Content-Type: Message/Rfc822\r\n");
//			this.source.append("Content-Description: Undelivered Message\r\n");
//			this.source.append("\r\n");
//			this.source.append(this.message);
//		}
//		this.source.append("\r\n\r\n");
//		this.source.append("--").append(this.mainBoundary).append("--");

		return this.source;
	}

	public ArrayList getReport(String charset) throws Exception {
		if (this.source == null)
			this.source = this.createReport(charset);
		ArrayList sourceList = new ArrayList();
		Collections.addAll(sourceList, this.source.toString().split("\r\n"));
		return sourceList;
	}

	public void saveTo(String filePath,String charet) throws Exception {
		if (this.source == null)
			this.source = this.createReport(charet);
		TextFile.save(filePath, this.source.toString());
	}

	public static void main(String[] args) throws Exception {
//	
	}

}
