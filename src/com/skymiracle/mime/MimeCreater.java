package com.skymiracle.mime;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.io.TextFile;
import com.skymiracle.util.Base64;
import com.skymiracle.util.CalendarUtil;
import com.skymiracle.util.StringUtils;
import com.skymiracle.util.UUID;
import com.skymiracle.util.UsernameWithDomain;

/**
 * get particular MIME message,create the MIME
 * 
 * @author Administrator
 * 
 */

public class MimeCreater {

	public static final int PRIORITY_LOW = 5;

	public static final int PRIORITY_NORMAL = 3;

	public static final int PRIORITY_HIGH = 1;

	public static final int TEXTTYPE_PLAIN = 10;

	public static final int TEXTTYPE_HTML = 20;

	public static final int TEXTTYPE_CSS = 30;

	public static final int TEXTTYPE_XML = 40;

	public static final int MULTIPART_MIXED = 10;

	public static final int MULTIPART_RELATED = 20;

	public static final int MULTIPART_ALTERNATIVE = 30;

	public static final int ENCODING_BASE64 = 10;

	public static final int ENCODING_QP = 20;

	public static final int ENCODING_7BIT = 30;

	public static final int ENCODING_8BIT = 40;

	public static final String MIME_VERSION = "1.0";

	private RichMailAddress from;

	private ArrayList<RichMailAddress> toList;

	private ArrayList<RichMailAddress> ccList;

	private ArrayList<RichMailAddress> bccList;

	private String subject;

	private String content;

	private String contentCharset;

	private int contentTransferEncoding;

	private int priority;

	private ArrayList<Attachment> attachmentList;

	private StringBuffer source;

	private String contentType;

	private String plainContent;

	private String mainBoundary;

	private String messageID;

	private int textType;

	private boolean receipt = false;

	private Calendar calendar = null;

	private String replyTo = "";

	private int subBoundaryTime = 0;

	private static String boundaryPre = "------=_Part_";

	private String boundaryC = "";

	private ArrayList<InnerAttachment> innerfilelist;

	/**
	 * init some MIME message
	 * 
	 * @param fromName
	 * @param fromEmail
	 */
	public MimeCreater() {
		this.toList = new ArrayList<RichMailAddress>();
		this.ccList = new ArrayList<RichMailAddress>();
		this.bccList = new ArrayList<RichMailAddress>();
		this.priority = MimeCreater.PRIORITY_NORMAL;
		this.subject = "";
		this.content = "";
		this.textType = MimeCreater.TEXTTYPE_HTML;
		this.contentCharset = "UTF-8";
		this.contentTransferEncoding = MimeCreater.ENCODING_BASE64;
		this.attachmentList = new ArrayList<Attachment>();
		this.innerfilelist = new ArrayList<InnerAttachment>();
		this.source = null;
		this.boundaryC = "_" + new UUID().toString();
		this.mainBoundary = boundaryPre + this.subBoundaryTime + this.boundaryC;
		this.messageID = new UUID().toString();
		this.calendar = Calendar.getInstance();
	}

	public void setFrom(String fromName, String fromEmail) {
		this.from = new RichMailAddress(fromName, fromEmail);
	}

	public void addTo(String toName, String toEmail) {
		this.toList.add(new RichMailAddress(toName, toEmail));
	}

	public void addCc(String ccName, String ccEmail) {
		this.ccList.add(new RichMailAddress(ccName, ccEmail));
	}

	public void addBcc(String bccName, String bccEmail) {
		this.bccList.add(new RichMailAddress(bccName, bccEmail));
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setContent(String content) {
		setContent(content, this.textType, this.contentCharset,
				this.contentTransferEncoding);
	}

	public void setTextType(int texttype) {
		this.textType = texttype;
	}

	public void setContent(String content, int TextType, String charset,
			int transferEncoding) {
		this.content = content;
		this.textType = TextType;
		this.contentCharset = charset;
		this.contentTransferEncoding = transferEncoding;
	}

	public void setReceipt(boolean receipt) {
		this.receipt = receipt;

	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * add the attachment
	 */
	public void addAttachment(String filePath, String fileName,
			String filePathName) {
		this.attachmentList
				.add(new Attachment(filePath, fileName, filePathName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.mime.MimeCreater#getMimeSource() add the inner
	 *      attacement
	 */
	public void addInnerAttachment(InnerAttachment innerfile) {
		this.innerfilelist.add(innerfile);
	}

	public StringBuffer getMimeSource() throws Exception {
		if (this.source == null)
			encodeMime();
		return this.source;
	}

	public void setCharset(String charset) {
		this.contentCharset = charset;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * create the MIME in memory
	 * 
	 * @throws Exception
	 */
	public String priorityToString(int priority) {
		switch (priority) {
		case MimeCreater.PRIORITY_HIGH:
			return "High";
		case MimeCreater.PRIORITY_NORMAL:
			return "Normal";
		case MimeCreater.PRIORITY_LOW:
			return "Low";
		default:
			return "Normal";
		}

	}

	private String multipartTypeToString(int multipartType) {
		switch (multipartType) {
		case MimeCreater.MULTIPART_MIXED:
			return "multipart/mixed";
		case MimeCreater.MULTIPART_RELATED:
			return "multipart/related";
		case MimeCreater.MULTIPART_ALTERNATIVE:
			return "Multipart/Alternative";
		default:
			return "multipart/mixed";
		}
	}

	private Object transferEncodingToString(int transferEncoding) {
		switch (transferEncoding) {
		case MimeCreater.ENCODING_BASE64:
			return "Base64";
		case MimeCreater.ENCODING_QP:
			return "Quoted-printable";
		case MimeCreater.ENCODING_7BIT:
			return "7bit";
		case MimeCreater.ENCODING_8BIT:
			return "8bit";
		default:
			return "Base64";
		}
	}

	private static String textTypeToString(int textType) {
		switch (textType) {
		case MimeCreater.TEXTTYPE_PLAIN:
			return "text/plain";
		case MimeCreater.TEXTTYPE_HTML:
			return "text/html";
		case MimeCreater.TEXTTYPE_CSS:
			return "text/css";
		case MimeCreater.TEXTTYPE_XML:
			return "text/xml";
		default:
			return "text/plain";
		}
	}

	private void encodeMime() throws Exception {
		this.source = new StringBuffer();
		// Append MIME-Version
		this.source.append("Mime-Version: ").append(MimeCreater.MIME_VERSION)
				.append("\r\n");

		// Append message-ID
		this.source.append("Message-ID: <").append(this.messageID).append(
				this.from.getMailAddress()).append(">\r\n");

		// Append Date
		// Calendar cldr = new GregorianCalendar();
		this.source.append("Date: ").append(
				CalendarUtil.getDateRFC822(this.calendar)).append("\r\n");

		// Append From
		this.source.append("From: \"").append(this.from.getDisplayName())
				.append("\" <").append(this.from.getMailAddress()).append(
						">\r\n");

		// Append reply-to
		if (!this.replyTo.equals(""))
			this.source.append("Reply-To: ").append(this.replyTo)
					.append("\r\n");

		// Append To
		this.source.append("To: ");
		for (int i = 0; i < this.toList.size(); i++) {
			this.source.append(((RichMailAddress) this.toList.get(i)).getSrc());
			if (i < this.toList.size() - 1)
				this.source.append(",");
		}
		this.source.append("\r\n");

		// Append Cc
		if (this.ccList.size() > 0) {
			this.source.append("Cc: ");
			for (int i = 0; i < this.ccList.size(); i++) {
				this.source.append(((RichMailAddress) this.ccList.get(i))
						.getMailAddress());
				if (i < this.ccList.size() - 1)
					this.source.append(",");
			}
			this.source.append("\r\n");
		}

		// Append Bcc
		if (this.bccList.size() > 0) {
			this.source.append("Bcc: ");
			for (int i = 0; i < this.bccList.size(); i++) {
				this.source.append(((RichMailAddress) this.bccList.get(i))
						.getMailAddress());
				if (i < this.bccList.size() - 1)
					this.source.append(",");
			}
			this.source.append("\r\n");
		}

		// Append Subject
		String subject = this.subject.length() == 0 ? "" : "=?"
				+ this.contentCharset
				+ "?B?"
				+ new String(Base64.encode(this.subject
						.getBytes(this.contentCharset))) + "?=";
		this.source.append("Subject: ").append(subject).append("\r\n");

		// Append ContentType
		if (this.attachmentList.size() == 0)
			this.contentType = multipartTypeToString(MimeCreater.MULTIPART_ALTERNATIVE);

		else
			this.contentType = multipartTypeToString(MimeCreater.MULTIPART_MIXED);
		// Append Boundary
		this.source.append("Content-Type: ").append(this.contentType).append(
				";\r\n\tboundary=\"").append(this.mainBoundary)
				.append("\"\r\n");
		// a group of X- attributes
		this.source.append("X-Mailer: Skymiracle Wdpost X\r\n");
		this.source.append("X-Priority: ").append(this.priority).append("\r\n");
		// this.source.append("X-RCPT-TO:
		// <").append(this.from.getMailAddress()).append(">\r\n");
		// this.source.append("X-Unsent: 1\r\n");
		if (this.receipt)
			this.source.append("Disposition-Notification-To: ").append(
					this.from.getSrc()).append("\r\n");

		// Append Space Line
		this.source.append("\r\n");
		// Append Plain Content
		String subBoundary = (String) this.mainBoundary;
		if (this.attachmentList.size() > 0) {
			subBoundary = boundaryPre + (++this.subBoundaryTime)
					+ this.boundaryC;
			this.source
					.append("--")
					.append(this.mainBoundary)
					.append("\r\n")
					.append("Content-Type: ")
					.append(
							multipartTypeToString(MimeCreater.MULTIPART_ALTERNATIVE))
					.append(";\r\n\tboundary=\"").append(subBoundary).append(
							"\"\r\n\r\n");

		}
		this.source.append("--").append(subBoundary).append("\r\n").append(
				"Content-Type: ").append(
				textTypeToString(MimeCreater.TEXTTYPE_PLAIN)).append(
				";charset=\"").append(this.contentCharset).append("\"\r\n")
				.append("Content-Transfer-Encoding: ").append(
						transferEncodingToString(this.contentTransferEncoding))
				.append("\r\n").append("Content-Disposition: inline\r\n")
				.append("\r\n").append(
						new String(Base64.encodeFormatted(this.plainContent
								.getBytes(this.contentCharset), 0, 76)))
				.append("\r\n");

		// Append HTML Content
		this.source.append("--").append(subBoundary).append("\r\n").append(
				"Content-Type: ").append(
				textTypeToString(MimeCreater.TEXTTYPE_HTML)).append(
				";charset=\"").append(this.contentCharset).append("\"\r\n")
				.append("Content-Transfer-Encoding: ").append(
						transferEncodingToString(this.contentTransferEncoding))
				.append("\r\n").append("Content-Disposition: inline\r\n")
				.append("\r\n").append(
						new String(Base64.encodeFormatted(this.content
								.getBytes(this.contentCharset), 0, 76)))
				.append("\r\n");
		// }

		this.source.append("--").append(subBoundary).append("--");
		if (this.attachmentList.size() > 0) {
			String filePath = null;
			String fileName = null;
			// add the attachment encode
			for (int i = 0; i < this.attachmentList.size(); i++) {
				Attachment attachment = (Attachment) this.attachmentList.get(i);
				filePath = attachment.getFilePath();
				fileName = "=?"
						+ this.contentCharset
						+ "?B?"
						+ new String(Base64.encode(attachment.getFileName()
								.getBytes(this.contentCharset))) + "?=";
				this.source
						.append("--")
						.append(this.mainBoundary)
						.append("\r\n")
						.append("Content-Type: application/octet-stream;")
						.append("name=\"")
						.append(fileName)
						.append("\"")
						.append("\r\n")
						.append("Content-Transfer-Encoding: ")
						.append(
								transferEncodingToString(this.contentTransferEncoding))
						.append("\r\n").append(
								"Content-Disposition: attachment;filename=\"")
						.append(fileName).append("\"").append("\r\n").append(
								"\r\n").append(
								new String(Base64.encodeFormatted(TextFile
										.loadBytes(filePath), 0, 76))).append(
								"\r\n");

			}
			this.source.append("--").append(this.mainBoundary).append("--");
		}
		this.source.append("\r\n\r\n");
		//
	}

	/**
	 * write the eml
	 */
	public void saveToFile(String filePath2) throws Exception {
		new File(filePath2).delete();

		StringBuffer source = new StringBuffer();

		// Append MIME-Version
		source.append("Mime-Version: ").append(MimeCreater.MIME_VERSION)
				.append("\r\n");

		// Append message-ID
		source.append("Message-ID: <").append(this.messageID).append(
				this.from.getMailAddress()).append(">\r\n");

		// Append Date
		// Calendar cldr = new GregorianCalendar();
		source.append("Date: ").append(
				CalendarUtil.getDateRFC822(this.calendar)).append("\r\n");
		// Append From
		String displayNameTmp = this.from.getDisplayName();
		String displayNameBM = displayNameTmp.length() == 0 ? "" : "=?"
				+ this.contentCharset
				+ "?B?"
				+ new String(Base64.encode(displayNameTmp
						.getBytes(this.contentCharset))) + "?=";
		source.append("From: \"").append(displayNameBM).append("\" <").append(
				this.from.getMailAddress()).append(">\r\n");
		// Append reply-to
		if (!this.replyTo.equals(""))
			source.append("Reply-To: ").append(this.replyTo).append("\r\n");

		// Append To
		source.append("To: ");
		for (int i = 0; i < this.toList.size(); i++) {
			RichMailAddress rma = (RichMailAddress) this.toList.get(i);
			String disn = rma.getDisplayName();
			String adr = rma.getMailAddress();
			disn = new StringBuffer().append("\"").append("=?").append(
					this.contentCharset).append("?B?").append(
					new String(Base64
							.encode(disn.getBytes(this.contentCharset))))
					.append("?=").append("\"").toString();
			adr = "<" + adr + ">";

			source.append(disn + " " + adr);
			if (i < this.toList.size() - 1)
				source.append(",");
		}
		source.append("\r\n");

		// Append Cc
		if (this.ccList.size() > 0) {
			source.append("Cc: ");
			for (int i = 0; i < this.ccList.size(); i++) {
				source.append(((RichMailAddress) this.ccList.get(i))
						.getMailAddress());
				if (i < this.ccList.size() - 1)
					source.append(",");
			}
			source.append("\r\n");
		}

		// Append Bcc
		if (this.bccList.size() > 0) {
			source.append("Bcc: ");
			for (int i = 0; i < this.bccList.size(); i++) {
				source.append(((RichMailAddress) this.bccList.get(i))
						.getMailAddress());
				if (i < this.bccList.size() - 1)
					source.append(",");
			}
			source.append("\r\n");
		}

		// Append Subject
		String subject = this.subject.length() == 0 ? "" : "=?"
				+ this.contentCharset
				+ "?B?"
				+ new String(Base64.encode(this.subject
						.getBytes(this.contentCharset))) + "?=";
		source.append("Subject: ").append(subject).append("\r\n");

		// Append ContentType
		if (this.attachmentList.size() == 0)
			this.contentType = multipartTypeToString(MimeCreater.MULTIPART_ALTERNATIVE);

		else
			this.contentType = multipartTypeToString(MimeCreater.MULTIPART_MIXED);
		// Append Boundary
		source.append("Content-Type: ").append(this.contentType).append(
				";\r\n\tboundary=\"").append(this.mainBoundary)
				.append("\"\r\n");
		// a group of X- attributes
		source.append("X-Mailer: Skymiracle Wdpost X\r\n");
		source.append("X-Priority: ").append(this.priority).append("\r\n");
		// source.append("X-RCPT-TO:
		// <").append(this.from.getMailAddress()).append(">\r\n");
		// source.append("X-Unsent: 1\r\n");
		if (this.receipt)
			source.append("Disposition-Notification-To: ").append(
					this.from.getSrc()).append("\r\n");

		// Append Space Line
		source.append("\r\n");
		String subBoundary = (String) this.mainBoundary;
		// Append Sub Header
		if (this.attachmentList.size() > 0) {
			subBoundary = boundaryPre + (++this.subBoundaryTime)
					+ this.boundaryC;
			source
					.append("--")
					.append(this.mainBoundary)
					.append("\r\n")
					.append("Content-Type: ")
					.append(
							multipartTypeToString(MimeCreater.MULTIPART_ALTERNATIVE))
					.append(";\r\n\tboundary=\"").append(subBoundary).append(
							"\"\r\n\r\n");

		}
		// Append Plain Content
		if (this.plainContent != null) {
			source
					.append("--")
					.append(subBoundary)
					.append("\r\n")
					.append("Content-Type: ")
					.append(textTypeToString(MimeCreater.TEXTTYPE_PLAIN))
					.append(";charset=\"")
					.append(this.contentCharset)
					.append("\"\r\n")
					.append("Content-Transfer-Encoding: ")
					.append(
							transferEncodingToString(this.contentTransferEncoding))
					.append("\r\n").append("Content-Disposition: inline\r\n")
					.append("\r\n").append(
							new String(Base64.encodeFormatted(this.plainContent
									.getBytes(this.contentCharset), 0, 76)))
					.append("\r\n");
		}
		// Append HTML Content
		source.append("--").append(subBoundary).append("\r\n").append(
				"Content-Type: ").append(
				textTypeToString(MimeCreater.TEXTTYPE_HTML)).append(
				";charset=\"").append(this.contentCharset).append("\"\r\n")
				.append("Content-Transfer-Encoding: ").append(
						transferEncodingToString(this.contentTransferEncoding))
				.append("\r\n").append("Content-Disposition: inline\r\n")
				.append("\r\n").append(
						new String(Base64.encodeFormatted(this.content
								.getBytes(this.contentCharset), 0, 76)))
				.append("\r\n");
		// }

		// Append innerattachmentfilelist
		if (this.innerfilelist.size() != 0) {
			source.append("--").append(subBoundary).append("--");
			source.append("\r\n\r\n");
			String filepath;
			String filename;
			for (int i = 0; i < this.innerfilelist.size(); i++) {
				filepath = ((InnerAttachment) this.innerfilelist.get(i))
						.getFilepath();
				filename = "=?"
						+ this.contentCharset
						+ "?B?"
						+ new String(Base64
								.encode(((InnerAttachment) this.innerfilelist
										.get(i)).getFilename().getBytes(
										this.contentCharset))) + "?=";
				source
						.append("--")
						.append(this.mainBoundary)
						.append("\r\n")
						.append("Content-Type: image/jpeg;")
						.append("\r\n")
						.append("\t")
						.append("name=\"")
						.append(filename)
						.append("\"")
						.append("\r\n")
						.append("Content-Transfer-Encoding: ")
						.append(
								transferEncodingToString(this.contentTransferEncoding))
						.append("\r\n").append("Content-ID: <").append(
								((InnerAttachment) this.innerfilelist.get(i))
										.getCid()).append(">").append("\r\n")
						.append("\r\n");
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						filePath2, true));
				bw.write(source.toString());
				bw.close();
				source.setLength(0);
				byte[] bytes = Base64.encodeFormatted(TextFile
						.loadBytes(filepath), 0, 76);
				FileOutputStream fos = new FileOutputStream(filePath2, true);
				fos.write(bytes);
				fos.close();
				source.append("\r\n");

			}
		}

		source.append("--").append(subBoundary).append("--");
		if (this.attachmentList.size() > 0) {
			source.append("\r\n\r\n");
			String filePath = null;
			String fileName = null;
			// add the attachment encode
			for (int i = 0; i < this.attachmentList.size(); i++) {
				Attachment attachment = (Attachment) this.attachmentList.get(i);
				filePath = attachment.getFilePath();
				fileName = "=?"
						+ this.contentCharset
						+ "?B?"
						+ new String(Base64.encode(attachment.getFileName()
								.getBytes(this.contentCharset))) + "?=";
				source
						.append("--")
						.append(this.mainBoundary)
						.append("\r\n")
						.append("Content-Type: application/octet-stream;")
						.append("name=\"")
						.append(fileName)
						.append("\"")
						.append("\r\n")
						.append("Content-Transfer-Encoding: ")
						.append(
								transferEncodingToString(this.contentTransferEncoding))
						.append("\r\n").append(
								"Content-Disposition: attachment;filename=\"")
						.append(fileName).append("\"").append("\r\n").append(
								"\r\n");
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						filePath2, true));
				bw.write(source.toString());
				bw.close();
				source.setLength(0);
				byte[] bytes = Base64.encodeFormatted(TextFile
						.loadBytes(filePath), 0, 76);
				FileOutputStream fos = new FileOutputStream(filePath2, true);
				fos.write(bytes);
				fos.close();
				source.append("\r\n");

			}
			source.append("--").append(this.mainBoundary).append("--");
		}
		source.append("\r\n\r\n");
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath2, true));
		bw.write(source.toString());
		bw.close();
	}

	public void setPlainContent(String plainContent) {
		this.plainContent = plainContent;
	}

	public class Attachment {
		private String filePath;

		private String fileName;

		private String filePathName;

		public Attachment(String filePath, String fileName, String filePathName) {
			this.filePath = filePath;
			this.fileName = fileName;
			this.filePathName = filePathName;
		}

		public String getFileName() {
			return this.fileName;
		}

		public String getFilePath() {
			return this.filePath;
		}

		public String getFilePathName() {
			return this.filePathName;
		}

	}

	public void setTos(String[] tos) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			addTo(rma.getDisplayName(), rma.getMailAddress());
		}
	}
	
	public void setTos(String[] tos, String defaultDomain) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			//添加默认域
			String mailAddress =  new UsernameWithDomain(rma.getMailAddress(), defaultDomain).toEmail();
			addTo(rma.getDisplayName(), mailAddress);
		}
	}
	

	public void setCcs(String[] tos) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			addCc(rma.getDisplayName(), rma.getMailAddress());
		}
	}
	
	public void setCcs(String[] tos, String defaultDomain) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			//添加默认域
			String mailAddress =  new UsernameWithDomain(rma.getMailAddress(), defaultDomain).toEmail();
			addCc(rma.getDisplayName(), mailAddress);
		}
	}

	public void setBccs(String[] tos) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			addBcc(rma.getDisplayName(), rma.getMailAddress());
		}
	}
	
	public void setBccs(String[] tos, String defaultDomain) {
		for (String to : tos) {
			if (to == null || to.trim().length() == 0)
				continue;
			RichMailAddress rma = new RichMailAddress(to);
			//添加默认域
			String mailAddress =  new UsernameWithDomain(rma.getMailAddress(), defaultDomain).toEmail();
			addBcc(rma.getDisplayName(), mailAddress);
		}
	}

	public List<String> getRcpts() {
		List<String> rcpts = new LinkedList<String>();
		for (RichMailAddress rma : this.toList) {
			rcpts.add(rma.getMailAddress());
		}
		for (RichMailAddress rma : this.ccList) {
			rcpts.add(rma.getMailAddress());
		}
		for (RichMailAddress rma : this.bccList) {
			rcpts.add(rma.getMailAddress());
		}

		return rcpts;
	}



} 
