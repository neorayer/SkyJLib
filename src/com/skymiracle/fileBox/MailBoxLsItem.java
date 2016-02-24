package com.skymiracle.fileBox;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mime.AttachmentFinder;
import com.skymiracle.mime.MimeHeader;
import com.skymiracle.mime.RichMailAddress;
import com.skymiracle.util.CalendarUtil;
import com.skymiracle.util.ViewTools;

public class MailBoxLsItem extends Mdo<MailBoxLsItem> {

	private String[] values;

	private String uuid;

	private String from;

	private String to;

	private String cc;

	private String bcc;

	private String subject;

	private boolean hasAttach;

	private boolean reply;

	private boolean readed;

	public String date;

	private long size;

	private long lastModified;

	private boolean starred;

	private String priority;

	public static final String[] csvLabels = { "uuid", "subject", "from", "to",
			"date", "lastModified", "size", "read", "starred", "attachment",
			"reply", "priority" };

	public MailBoxLsItem() {
		super();
	}

	public MailBoxLsItem(String uuid, MimeHeader mimeHeader, FileInfo fileInfo)
			throws IOException {
		this.uuid = uuid;
		this.subject = mimeHeader.getSubject();
		this.from = mimeHeader.getFrom().toString();
		this.hasAttach = AttachmentFinder.findAttachment(fileInfo
				.getAbsolutePath());
		this.to = richMailAddressesToStr(mimeHeader.getTo());
		this.lastModified = fileInfo.lastModified();
		this.date = CalendarUtil.normalParse(mimeHeader.getDate());
		if (this.date == null)
			this.date = CalendarUtil.getHumanFormat(this.lastModified);
		this.size = fileInfo.length();
		this.readed = fileInfo.getName().endsWith(",S");
		this.starred = fileInfo.getName().startsWith("starred");
		this.reply = fileInfo.getName().endsWith("!3,S");
		this.priority = mimeHeader.getPriority();
		if (this.priority == null)
			this.priority = "3";

		this.values = new String[12];
		this.values[0] = this.uuid;
		this.values[1] = this.subject;
		this.values[2] = this.from;
		this.values[3] = this.to;
		this.values[4] = this.date;
		this.values[5] = Long.toString(this.lastModified);
		this.values[6] = Long.toString(this.size);
		this.values[7] = this.readed ? "1" : "0";
		this.values[8] = this.starred ? "1" : "0";
		this.values[9] = this.hasAttach ? "1" : "0";
		this.values[10] = this.reply ? "1" : "0";
		this.values[11] = this.priority;
	}

	public MailBoxLsItem(String[] values) {
		this.values = values;
		this.uuid = values[0];
		this.subject = values[1];
		this.from = values[2];
		this.to = values[3];
		this.date = values[4];
		try {
			this.lastModified = Long.parseLong(values[5]);
		} catch (Exception e) {
			this.lastModified = System.currentTimeMillis();
		}
		try {
			this.size = Long.parseLong(values[6]);
		} catch (Exception e) {
			this.size = 0;
		}
		this.readed = this.values[7].equals("1");
		this.starred = this.values[8].equals("1");
		if (values.length > 9) {
			this.hasAttach = this.values[9].equals("1");
			this.reply = this.values[10].equals("1");
			this.priority = this.values[11];
		}
	}

	public String richMailAddressesToStr(List<RichMailAddress> rmas) {
		StringBuffer sb = new StringBuffer();
		for (RichMailAddress rma : rmas)
			sb.append(rma.toString()).append(',');
		return sb.toString();
	}

	public String getBcc() {
		return this.bcc;
	}

	public String getCc() {
		return this.cc;
	}

	public String getFrom() {
		return this.from;
	}

	public boolean getHasAttach() {
		return this.hasAttach;
	}

	public void setHasAttach(boolean hasAttach) {
		this.hasAttach = hasAttach;
	}

	public long getLastModified() {
		return this.lastModified;
	}

	public boolean getReaded() {
		return this.readed;
	}

	public long getSize() {
		return this.size;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getTo() {
		return this.to;
	}

	public String getToDisplayName() {
		RichMailAddress rma = new RichMailAddress(to);
		return rma.getDisplayName();
	}

	public String getDate() {
		return this.date;
	}

	public boolean getStarred() {
		return this.starred;
	}

	public String[] getCsvValues() {
		return this.values;
	}

	public boolean getReply() {
		return this.reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public String getPriority() {
		if (priority == null || priority.equals(""))
			priority = "5";
		if (priority.length() > 1) {
			priority = priority.substring(0, 1);
			try {
				Integer.parseInt(priority);
			} catch (Exception e) {
				priority = "5";
			}
		}
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void putValues(String[] values) {
		this.values = values;
	}

	public Date getLastModifiedDate() {
		return new Date(lastModified);
	}

	public String getTgmkSize() {
		return ViewTools.getTgmkSize(size);
	}

	public String getFromDisplayName() {
		RichMailAddress rma = new RichMailAddress(from);
		return rma.getDisplayName();
	}

	public boolean getIsToday() {
		return CalendarUtil.isToday(lastModified);
	}

	public boolean getIsThisWeek() {
		return CalendarUtil.isThisWeek(lastModified);
	}

	public String getLocalDateTime() {
		return CalendarUtil.getHumanFormat(this.lastModified);
	}

	@Override
	public String[] keyNames() {
		return new String[] { "uuid" };
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

}
