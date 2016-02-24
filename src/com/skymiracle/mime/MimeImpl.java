package com.skymiracle.mime;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.csv.Csv;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.BytesLinesSectionImpl;
import com.skymiracle.util.LinesSection;
import com.skymiracle.util.UUID;
import com.skymiracle.util.Rfc2047Codec.CodeRes;

/**
 * save the eml file
 * 
 * @author Administrator
 * 
 */
public class MimeImpl implements Mime {
	private LinesSection headLs;

	private LinesSection bodyLs;

	protected MimeHeader header;

	protected List<Mime> subMimeList;

	private String UUIDStr;

	private String contentTextHtml = null;

	private String contentTextPlain = null;

	private List<Attachment> downableAttachmentList = null;

	private List<Attachment> innerAttachmentList = null;

	private String saveDirPath;

	private String saveHeaderPath;

	private String saveSingleBodyPath;

	private String saveContentHtmlInfoPath;

	private String saveDownableAttInfoPath;

	private String saveInnerAttInfoPath;

	private String saveContentPlainInfoPath;

	/**
	 * init the MimeImpl,
	 * 
	 * @param filePath
	 * @param useBytes
	 *            useBytes is true is faster than useString very much.
	 * @throws Exception
	 * 
	 * @notes useBytes is not useful.
	 */
	public MimeImpl(String filePath, boolean useBytes) throws Exception {
		initParse(new BytesLinesSectionImpl(filePath));
	}

	public MimeImpl(LinesSection ls) {
		initParse(ls);
	}

	// create mime from a cache dirPath
	public MimeImpl(String dirPath) throws IOException {
		buildSavePath(dirPath);
		this.header = new MimeHeaderImpl(this.saveDirPath + "/header");
	}

	private void initParse(LinesSection ls) {
		this.UUIDStr = (new UUID()).toShortString();
		this.headLs = ls.getFirstSection();
		this.bodyLs = ls.getNoFirstSection();

		this.header = new MimeHeaderImpl(this.headLs);

		String majorType = this.header.getContentType().getMajor();

		// Deal with content-id
		// if has content-id, then the uuid= content-id
		String contentId = this.header.getAttrValue("content-id");
		if (contentId != null) {
			if (!contentId.equals("")) {
				if (contentId.startsWith("<") && contentId.endsWith(">"))
					contentId = contentId.substring(1, contentId.length() - 1);
				this.UUIDStr = contentId;
			}
		}
		if (majorType.equalsIgnoreCase("multipart"))
			this.subMimeList = buildSubMimeList();

	}

	private List<Mime> buildSubMimeList() {
		LinesSection[] bodyLss = this.bodyLs.getSections(this.header
				.getContentType().getBoundary());
		List<Mime> bpList = new LinkedList<Mime>();
		for (int i = 0; i < bodyLss.length; i++) {
			Mime bodyPart = new MimeImpl(bodyLss[i]);
			bpList.add(bodyPart);
		}
		return bpList;
	}

	private void saveSingleBody() throws Exception {
		String filePath = this.saveSingleBodyPath;
		if (this.getContentTransferEncoding().equalsIgnoreCase("base64")) {
			TextFile.decodeBase64(this.getBodyLs(), filePath);
		} else if (this.getContentTransferEncoding().equalsIgnoreCase(
				"quoted-printable")) {
			TextFile.decodeQP(this.getBodyLs(), filePath, this.getContentType()
					.getCharset());
		} else
			this.getBodyLs().save(filePath);
	}

	private void saveBodyInfo() throws IOException {
		String majorType = getContentType().getMajor();
		String secondType = getContentType().getSecond();
		String contentTypeStr = majorType + "/" + secondType;
		String contentId = this.header.getAttrValue("content-id");
		ContentDisposition cd = getContentDisposition();
		String cdMajor = cd == null ? null : cd.getMajor();
		// String charset = getContentType().getCharset();
		// if (charset.trim().length() == 0)
		// charset = null;
		// else{
		// try {
		// new String("".getBytes(), charset);
		// }catch(Exception e) {
		// Logger.warn("", e);
		// charset = null;
		// }
		// }
		if (cdMajor != null && cdMajor.equalsIgnoreCase("attachment")) {
			saveDownableAttInfo();
			return;
		}
		if (majorType.equalsIgnoreCase("application")) {
			saveDownableAttInfo();
			return;
		}
		if (majorType.equalsIgnoreCase("message")) {
			if (secondType.equalsIgnoreCase("delivery-status")) {
				saveDownableAttInfo("delivery_status.dat");
			} else if (secondType.equalsIgnoreCase("rfc822")) {
				saveDownableAttInfo("unknown.eml");
			} else
				saveInnerAttInfo();
			return;
		}
		if (contentId.length() != 0) {
			saveInnerAttInfo();
			return;
		}
		if (cdMajor != null && cdMajor.equalsIgnoreCase("inline")
				&& cd.getFileNameCodeRes().content.length() != 0) {
			saveDownableAttInfo();
			return;
		}
		if (majorType == null || majorType.equals(""))
			saveContentPlainInfo();
		else if (majorType.equalsIgnoreCase("text")) {
			if (contentTypeStr.equalsIgnoreCase("text/html"))
				saveContentHtmlInfo();
			else
				saveContentPlainInfo();
		} else {
			saveInnerAttInfo();
		}
	}

	private void saveContentHtmlInfo() throws IOException {
		Csv csv = new Csv(this.saveContentHtmlInfoPath, "ISO-8859-1");
		String[] values = new String[2];
		values[0] = this.UUIDStr;
		values[1] = getContentType().getCharset();
		csv.insert(values);
	}

	private void saveContentPlainInfo() throws IOException {
		Csv csv = new Csv(this.saveContentPlainInfoPath, "ISO-8859-1");
		String[] values = new String[2];
		values[0] = this.UUIDStr;
		values[1] = getContentType().getCharset();
		csv.insert(values);
	}

	private void saveDownableAttInfo() throws IOException {
		String[] values = new String[5];
		values[0] = this.UUIDStr;
		String itemCharset = "ISO-8859-1";
		if (this.header.getContentDisposition() == null)
			values[1] = this.header.getContentType().getAttrValue("name");
		else {
			CodeRes codeRes = this.header.getContentDisposition()
					.getFileNameCodeRes();
			values[1] = codeRes.content;
			itemCharset = codeRes.charset;
			if (codeRes.charset == null)
				itemCharset = "ISO-8859-1"; // TODO: FIXME!!!
		}
		values[2] = "" + new File(this.saveSingleBodyPath).length();
		values[3] = getContentType().getMajor() + "/"
				+ getContentType().getSecond();
		values[4] = itemCharset;
		Csv csv = null;
		csv = new Csv(this.saveDownableAttInfoPath, itemCharset);
		csv.insert(values);
	}

	private void saveDownableAttInfo(String downAttName)
			throws IOException {
		Csv csv = new Csv(this.saveDownableAttInfoPath, "ISO-8859-1");
		String[] values = new String[5];
		values[0] = this.UUIDStr;
		values[1] = downAttName;
		values[2] = "" + new File(this.saveSingleBodyPath).length();
		values[3] = getContentType().getMajor() + "/"
				+ getContentType().getSecond();
		values[4] = "ISO-8859-1";
		csv.insert(values);
	}

	private void saveInnerAttInfo() throws IOException {
		Csv csv = new Csv(this.saveInnerAttInfoPath, "ISO-8859-1");
		String[] values = new String[4];
		values[0] = this.UUIDStr;
		values[1] = this.header.getContentDisposition() == null ? ""
				: this.header.getContentDisposition().getFileNameCodeRes().content;
		values[2] = "" + new File(this.saveSingleBodyPath).length();
		values[3] = getContentType().getMajor() + "/"
				+ getContentType().getSecond();
		csv.insert(values);
	}

	private void buildSavePath(String dirPath) {
		this.saveDirPath = dirPath;
		this.saveHeaderPath = this.saveDirPath + "/header." + this.UUIDStr;
		this.saveSingleBodyPath = this.saveDirPath + "/body." + this.UUIDStr;
		this.saveContentHtmlInfoPath = this.saveDirPath + "/content-html.csv";
		this.saveContentPlainInfoPath = this.saveDirPath + "/content-plain.csv";
		this.saveDownableAttInfoPath = this.saveDirPath + "/downable-atts.csv";
		this.saveInnerAttInfoPath = this.saveDirPath + "/inner-atts.csv";
	}

	private void initSaveInfoFile() throws IOException {
		this.headLs.save(this.saveDirPath + "/header");
		String[] contentHtmlInfoLabels = { "UUID", "CHARSET" };
		String[] contentPlainInfoLabels = { "UUID", "CHARSET" };
		String[] downableAttLabels = { "UUID", "FILENAME", "FILESIZE",
				"CHARSET" };
		String[] innerAttInfoLabels = { "UUID", "FILENAME", "FILESIZE" };
		new Csv(this.saveContentHtmlInfoPath, contentHtmlInfoLabels,
				"ISO-8859-1");
		new Csv(this.saveContentPlainInfoPath, contentPlainInfoLabels,
				"ISO-8859-1");
		new Csv(this.saveDownableAttInfoPath, downableAttLabels, "ISO-8859-1");
		new Csv(this.saveInnerAttInfoPath, innerAttInfoLabels, "ISO-8859-1");
	}

	public String save(String dirPath) throws Exception {
		buildSavePath(dirPath);
		initSaveInfoFile();
		realSave(dirPath);
		return this.UUIDStr;
	}

	private void realSave(String dirPath) throws Exception {

		buildSavePath(dirPath);

		this.headLs.save(this.saveHeaderPath);

		if (this.subMimeList == null) {
			saveSingleBody();
			saveBodyInfo();
		} else {
			for (int i = 0; i < this.subMimeList.size(); i++) {
				MimeImpl subMime = (MimeImpl) this.subMimeList.get(i);
				subMime.realSave(dirPath);
			}
		}
	}

	private void loadContentHtmlInfo() throws IOException {
		this.contentTextHtml = "";
		Csv csv = new Csv(this.saveContentHtmlInfoPath, "ISO-8859-1");
		ArrayList<String[]> lineList = csv.getLineList();
		if (lineList.size() < 1)
			return;
		String[] values = lineList.get(0);
		String filePath = this.saveDirPath + "/body." + values[0];
		String charset = values[1];
		byte[] bs = StreamPipe.fileToBytes(new File(filePath));
		try {
			this.contentTextHtml = new String(bs, charset);
			this.contentTextHtml = this.contentTextHtml.replaceAll("cid:",
					("__mailInnerImg.img?path=" + this.saveDirPath
							+ "&uid=" + "/body."));
		} catch (UnsupportedEncodingException e) {
			// TODO: if charset no found or error, default set to GBK.
			this.contentTextHtml = new String(bs, "GBK");
		}

	}

	private void loadContentPlainInfo() throws IOException {
		this.contentTextPlain = "";
		Csv csv = new Csv(this.saveContentPlainInfoPath, "ISO-8859-1");
		ArrayList<String[]> lineList = csv.getLineList();
		if (lineList.size() < 1)
			return;
		String[] values = lineList.get(0);
		String filePath = this.saveDirPath + "/body." + values[0];
		String charset = values[1];

		byte[] bs = StreamPipe.fileToBytes(new File(filePath));
		try {
			this.contentTextPlain = new String(bs, charset);
		} catch (UnsupportedEncodingException e) {
			this.contentTextPlain = new String(bs, "GBK");
		}
		this.contentTextPlain = this.contentTextPlain.replaceAll("\r\n", "<br />");

		for (int i = 1; i < lineList.size(); i++) {
			values = lineList.get(i);
			filePath = this.saveDirPath + "/body." + values[0];
			charset = values[1];
			bs = StreamPipe.fileToBytes(new File(filePath));
			try {
				this.contentTextPlain = new String(bs, charset)
						+ "--------------------------------------\r\n"
						+ this.contentTextHtml;
			} catch (UnsupportedEncodingException e) {
				this.contentTextPlain = new String(bs, "GBK")
						+ "--------------------------------------\r\n"
						+ this.contentTextPlain;
			}
		}
	}

	private void loadDownableAttInfo() throws IOException {
		this.downableAttachmentList = new LinkedList<Attachment>();
		Csv csv = new Csv(this.saveDownableAttInfoPath, "ISO-8859-1");
		ArrayList<String[]> lineList = csv.getLineList();
		for (String[] values : lineList) {
			String charset = values[4];
			loadDownableAttInfo(charset);
			break;
		}
	}

	private void loadDownableAttInfo(String charset) throws IOException {
		this.downableAttachmentList = new LinkedList<Attachment>();
		Csv csv = new Csv(this.saveDownableAttInfoPath, charset);
		ArrayList<String[]> lineList = csv.getLineList();
		for (String[] values : lineList) {
			String filePath = this.saveDirPath + "/body." + values[0];
			String fileName = values[1];
			long fileSize = Long.parseLong(values[2]);
			String contentType = values[3];
			if("ISO-8859-1".equalsIgnoreCase(charset))
				fileName = new String(fileName.getBytes("ISO-8859-1"), "GBK");
			Attachment attachment = new Attachment(filePath, fileName,
					fileSize, contentType);
			this.downableAttachmentList.add(attachment);
		}
	}

	private void loadInnerAttInfo() throws IOException {
		this.innerAttachmentList = new LinkedList<Attachment>();
		Csv csv = new Csv(this.saveInnerAttInfoPath, "ISO-8859-1");
		ArrayList<String[]> lineList = csv.getLineList();
		for (String[] values : lineList) {
			String filePath = this.saveDirPath + "/body." + values[0];
			String contentType = values[3];
			Attachment attachment = new Attachment(filePath, values[1], Integer
					.parseInt(values[2]), contentType);
			this.innerAttachmentList.add(attachment);
		}
	}

	public MimeHeader getHeader() {
		return this.header;
	}

	public List<Mime> getSubMimeList() {
		return this.subMimeList;
	}

	public String getHeaderStr() {
		return this.headLs.toString();
	}

	public String getBodyStr() {
		return this.bodyLs.toString();
	}

	public LinesSection getBodyLs() {
		return this.bodyLs;
	}

	public RichMailAddress getFrom() {
		return this.header.getFrom();
	}

	public String getReplyMail() {
		String replyTo = this.header.getAttrValue("reply-to");
		if (replyTo == null || replyTo.trim().equals(""))
			replyTo = getFrom().getMailAddress();
		return replyTo;
	}

	public List<RichMailAddress> getTo() {
		return this.header.getTo();
	}

	public List<RichMailAddress> getCc() {
		return this.header.getCc();
	}

	public List<RichMailAddress> getBcc() {
		return this.header.getBcc();
	}

	public String getSubject() {
		return this.header.getSubject();
	}

	public String getDate() {
		return this.header.getDate();
	}

	public String getContentTextHtml() {
		if (this.contentTextHtml == null)
			try {
				loadContentHtmlInfo();
			} catch (IOException e) {
				Logger.error("", e);
			}
		return this.contentTextHtml;
	}

	public String getContentTextPlain() {
		if (this.contentTextPlain == null)
			try {
				loadContentPlainInfo();
			} catch (IOException e) {
				Logger.error("", e);
			}
		return this.contentTextPlain;
	}

	public List<Attachment> getDownableAttachments() {
		if (this.downableAttachmentList == null)
			try {
				loadDownableAttInfo();
			} catch (IOException e) {
				Logger.error("MimeImpl.getDownlableAttachements", e);
			}
		return this.downableAttachmentList;
	}

	public List<Attachment> getInnerAttachments() {
		if (this.innerAttachmentList == null)
			try {
				loadInnerAttInfo();
			} catch (IOException e) {
				Logger.error("MimeImpl.getInnerAttachements", e);
			}
		return this.innerAttachmentList;
	}

	public ContentType getContentType() {
		return this.header.getContentType();
	}

	public String getContentTransferEncoding() {
		return this.header.getContentTransferEncoding();
	}

	public ContentDisposition getContentDisposition() {
		return this.header.getContentDisposition();
	}

	public String getAttrValue(String name) {
		return this.header.getAttrValue(name);
	}

}
