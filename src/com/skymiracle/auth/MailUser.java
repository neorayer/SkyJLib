package com.skymiracle.auth;

import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.Mdo_X;
import com.skymiracle.mdo5.Store;
import com.skymiracle.mdo5.Mdo.Title;
import com.skymiracle.sor.exception.AppException;

public abstract class MailUser<T extends MailUser<T>> extends Mdo<T> {
	public static final int FORWARD_NO = 0;

	public static final int FORWARD_YES_NOSAVE = 1;

	public static final int FORWARD_YES_SAVE = 2;

	public static final int AUTOREPLY_INT_NO = 0;

	public static final int AUTOREPLY_INT_YES = 1;

	public static final int ADDRBOOK_FILTER_YES = 1;

	public static final int ADDRBOOK_FILTER_NO = 0;

	public static final String STATUS_OPEN = "open"; //

	public static final String STATUS_PAUSE = "pause"; //

	public static final String STATUS_SLEEP = "sleep"; //

	public static final String STATUS_HOLD = "hold"; //

	public static final String STATUS_STOP = "stop"; //

	public static final int DEFAULT_MNPP = 20;

	public static final long SPACEALERT_NO = 0;

	public static final String STYLE_DEFAULT = "oa";

	public static final int NOTIFY_CLOSE = 0;

	public static final int NOTIFY_APPLY_AUTH = 1;

	public static final int NOTIFY_CANCEL_AUTH = 2;

	public static final int NOTIFY_OPEN = 3;

	@Title("用户名")
	protected String uid;

	@Title("域名")
	protected String dc;

	@Title("密码")
	protected String userPassword;

	@Title("自动回复")
	@Desc("0: 关闭 ｜ 1: 开启")
	protected String autoreplyswitch;

	@Title("自动回复内容")
	protected String autoreplycontent;

	@Title("是否转发")
	@Desc("0: 不转发 ｜ 1: 转发不保存 ｜ 2: 转发并保存")
	protected int forward;

	@Title("转发地址")
	protected String[] forwardaddr;

	@Title("最大发送人数")
	protected int maxcc = 1;

	@Title("最大邮件大小")
	protected long messageSize;

	// 收信时拒收的域集
	protected String[] rejectdomain;

	// 收信时拒收的Email集
	protected String[] rejectemail;

	// 收信时拒收的标题集
	protected String[] rejectesubject;
	
	@Title("邮箱空间(b)")
	protected long size;

	@Title("是否开通SMTP")
	protected String issmtp;

	@Title("是否开通POP3")
	protected String ispop3;

	@Title("空间报警线")
	protected long spaceAlert;

	@Title("状态")
	@Desc("open:开通, pause:暂停, closed:关闭")
	protected String status;

	@Title("存储服务器地址")
	protected String storageLocation;

	protected String replyMail;

	@Title("每页显示多少封邮件")
	protected int mnpp;

	@Title("是否自动保存已发送 1:自动 0:不自动")
	protected String savenew;

	protected String sendername;

	// According to mail user's person addressbook to filter the Email.
	private int addrbookfilter;

	public MailUser(Mdo_X<T> x) {
		super(x);
	}

	public MailUser(String dc, String uid) {
		this.dc = dc;
		this.uid = uid;
	}

	public MailUser(String dc) {
		this.dc = dc;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "uid", "dc" };
	}

	public String selfDN() throws NullKeyException {
		return "uid=" + this.uid;
	}

	public String getSendername() {
		return sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

	public String getAutoreplycontent() {
		return autoreplycontent;
	}

	public void setAutoreplycontent(String autoreplycontent) {
		this.autoreplycontent = autoreplycontent;
	}

	public int getMnpp() {
		return mnpp;
	}

	public void setMnpp(int mnpp) {
		this.mnpp = mnpp;
	}

	public String getSavenew() {
		return savenew;
	}

	public void setSavenew(String savenew) {
		this.savenew = savenew;
	}

	public int getMaxcc() {
		return this.maxcc;
	}

	public void setMaxcc(int maxcc) {
		this.maxcc = maxcc;
	}

	public int getForward() {
		return this.forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

	public String[] getForwardaddr() {
		return this.forwardaddr;
	}

	public void setForwardaddr(String[] forwardaddr) {
		this.forwardaddr = forwardaddr;
	}

	public String fatherDN(String baseDN) throws NullKeyException {
		return baseDN;
	}

	public String getAutoreplyswitch() {
		return this.autoreplyswitch;
	}

	public void setAutoreplyswitch(String autoreplyswitch) {
		this.autoreplyswitch = autoreplyswitch;
	}

	public long getSpaceAlert() {
		return this.spaceAlert;
	}

	public void setSpaceAlert(long spaceAlert) {
		this.spaceAlert = spaceAlert;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getMessageSize() {
		return this.messageSize;
	}

	public void setMessageSize(long messageSize) {
		this.messageSize = messageSize;
	}

	public String getDc() {
		return this.dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public String getIssmtp() {
		return this.issmtp;
	}

	public void setIssmtp(String issmtp) {
		this.issmtp = issmtp;
	}

	public String[] getRejectdomain() {
		return this.rejectdomain;
	}

	public void setRejectdomain(String[] rejectdomain) {
		this.rejectdomain = rejectdomain;
	}

	public String[] getRejectemail() {
		return this.rejectemail;
	}

	public void setRejectemail(String[] rejectemail) {
		this.rejectemail = rejectemail;
	}

	public String[] getRejectesubject() {
		return this.rejectesubject;
	}

	public void setRejectesubject(String[] rejectesubject) {
		this.rejectesubject = rejectesubject;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStorageLocation() {
		return this.storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public boolean isRejectDomain(String fromDomain) {
		if (this.rejectdomain == null)
			return false;
		for (int i = 0; i < this.rejectdomain.length; i++)
			if (fromDomain.equalsIgnoreCase(this.rejectdomain[i]))
				return true;
		return false;
	}

	public boolean isRejectEmail(String mailFrom) {
		if (this.rejectemail == null)
			return false;
		for (int i = 0; i < this.rejectemail.length; i++)
			if (mailFrom.equalsIgnoreCase(this.rejectemail[i]))
				return true;
		return false;
	}

	public boolean isRejectSubject(String subject) {
		if (this.rejectesubject == null)
			return false;
		for (String item : rejectesubject) {
			if (item.trim().length() == 0)
				continue;
			if (subject.indexOf(item) != -1)
				return true;
		}
		return false;
	}

	public boolean isAutoreply() {
		if (this.autoreplyswitch == null)
			return false;
		return this.autoreplyswitch.equalsIgnoreCase("" + AUTOREPLY_INT_YES);
	}

	public boolean isOpen() {
		return STATUS_OPEN.equalsIgnoreCase(this.status) || this.status == null;
	}

	public String getReplyMail() {
		return this.replyMail;
	}

	public void setReplyMail(String replyMail) {
		this.replyMail = replyMail;
	}

	public String getIspop3() {
		return ispop3;
	}

	public void setIspop3(String ispop3) {
		this.ispop3 = ispop3;
	}

	public String toEmail() {
		return this.uid + '@' + this.dc;
	}

	public int getAddrbookfilter() {
		return addrbookfilter;
	}

	public void setAddrbookfilter(int addrbookfilter) {
		this.addrbookfilter = addrbookfilter;
	}
	
	public abstract static class X<T extends MailUser<T>> extends Mdo_X<T> {
		public X(Class<T> mdoClass, Store store) {
			super(mdoClass, store);
		}
	}

	public abstract boolean isGroupUser() throws AppException, Exception ;
}
