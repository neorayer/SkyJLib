package com.skymiracle.mime;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.io.TextFile;
import com.skymiracle.util.CalendarUtil;
import com.skymiracle.util.LinesSection;
import com.skymiracle.util.Rfc2047Codec;
import com.skymiracle.util.StringLinesSectionImpl;


public class MimeHeaderImpl implements MimeHeader {
	private LinesSection ls;

	private Hashtable<String, String> ht;

	private ContentType contentType = null;

	public MimeHeaderImpl(LinesSection ls) {
		this.ls = ls;
		this.ht = new Hashtable<String, String>();
		parse();
		if (getDate() == null || getDate().equals("null")) {
			this.ht.put("date", "");
		}
	}

	public MimeHeaderImpl(String filePath) throws IOException {
		this(new StringLinesSectionImpl(TextFile
				.loadFirstSectionLinesList(filePath)));
	}

	public MimeHeaderImpl() {
		this.ht = new Hashtable<String, String>();
	}

	public void addAttrValue(String name, String value) {
		this.ht.put(name, value);
	}

	private void parse() {
		// TODO in gmail ,it can't parse correct.
		// because gmail or yahoo mail include DomainKey-Signature
		// at the same time,such as 'received',its numbers maybe > 1
		// but we couldn't use these;
		for (int i = this.ls.getBegin(); i < this.ls.getEnd(); i++) {

			String line = this.ls.getStringLine(i, "ISO-8859-1");
			int ColonPos = line.indexOf(':');
			if (ColonPos < 0) {
				continue;
			}

			String name = line.substring(0, ColonPos).toLowerCase().trim();
			String value = line.substring(ColonPos + 1).trim();
			for (int j = i + 1; j < this.ls.getEnd(); j++) {
				String line2 = this.ls.getStringLine(j, "utf-8");
				if (isFrontContinues(line2))
					value += line2.trim();
				else
					break;

			}
			this.ht.put(name, value);
		}

	}

	private boolean isFrontContinues(String s) {
		if (s.startsWith(" ") || s.startsWith("\t") || s.indexOf(':') < 0) {
			return true;
		} else
			return false;
	}

	public boolean isMultipart() {
		// TODO Auto-generated method stub
		return false;
	}

	public ContentType getContentType() {
		if (this.contentType == null)
			this.contentType = new ContentTypeImpl((String) this.ht
					.get("content-type"));
		return this.contentType;
	}

	public ContentDisposition getContentDisposition() {
		String s = getAttrValue("Content-Disposition");
		if (s.length() == 0)
			return null;
		return new ContentDispositionImpl(s);
	}

	public String getContentLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentTransferEncoding() {
		return getAttrValue("content-transfer-encoding");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public RichMailAddress getFrom() {
		return new RichMailAddress((String) this.ht.get("from"));
	}

	/**
	 * get RichMailAddress[] from the input string of the receiver message
	 * 
	 * @param s
	 * @return to
	 */
	private List<RichMailAddress> StrsToRichMailAddresses(String s) {
		List<RichMailAddress> tos = new LinkedList<RichMailAddress>();
		if (s == null)
			return tos;
		String[] ss = s.split(",");
		for (String item : ss)
			tos.add(new RichMailAddress(item));
		return tos;
	}

	public List<RichMailAddress> getTo() {
		return StrsToRichMailAddresses((String) this.ht.get("to"));
	}

	public List<RichMailAddress> getCc() {
		return StrsToRichMailAddresses((String) this.ht.get("cc"));
	}

	public List<RichMailAddress> getBcc() {
		return StrsToRichMailAddresses((String) this.ht.get("bcc"));
	}

	public String getDate() {
		return (String) this.ht.get("date");
	}
	
	public String getLocalTime(){
		return CalendarUtil.normalParse(this.getDate())+":00";
	}
	
	public Date getLocalDate() {
		return CalendarUtil.stringToDate(this.getLocalTime(), "yyyy-MM-dd HH:mm:ss");
	}
	
	public String getSubject() {
		String subject = (String) this.ht.get("subject");
		if (subject == null)
			return "";
		subject = Rfc2047Codec.decode(subject);
		return subject;
	}

	public String getAttrValue(String attrName) {
		Object value = this.ht.get(attrName.toLowerCase());
		if (value == null)
			return "";
		else
			return (String) value;
	}

	public String getContentId() {
		Object value = this.ht.get("content-id");
		if (value == null)
			return "";
		else
			return ((String) value).trim();

	}

	public String getPriority() {
		String priority = (String) this.ht.get("x-priority");
		if(priority == null || priority.equals(""))
			priority = "5";
		if(priority.length() > 1) {
			priority = priority.substring(0, 1);
			try{
				Integer.parseInt(priority);
			}catch(Exception e) {
				priority = "5";
			}
		}
		return priority;
	}
}
