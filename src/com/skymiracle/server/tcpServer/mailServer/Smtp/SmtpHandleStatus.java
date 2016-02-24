package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.auth.MailUser;

public class SmtpHandleStatus {

	SmtpServer smtpServer;

	// 命令当前执行位置
	public static final int CMDPOS_START = 0;
	public static final int CMDPOS_AFTER_EHLO = 10;
	public static final int CMDPOS_AFTER_HELO = 20;
	public static final int CMDPOS_AFTER_AUTH = 30;
	public static final int CMDPOS_AFTER_USERNAME = 40;
	public static final int CMDPOS_AFTER_PASSWORD = 50;
	public static final int CMDPOS_AFTER_MAIL = 60;
	public static final int CMDPOS_AFTER_RCPT = 70;
	public static final int CMDPOS_AFTransaction = 80;
	public static final int CMDPOS_AFTER_DATADOT = 90;
	private int cmdpos = CMDPOS_START;

	// hello类型
	public static final int HELLO_TYPE_NO = -1;
	public static final int HELLO_TYPE_HELO = 0;
	public static final int HELLO_TYPE_EHLO = 10;
	private int heloType = HELLO_TYPE_NO;

	private String heloName = "";

	private MailUser fromMailUser;

	// 授权用户
	private MailUser authMailUser;

	private boolean isSmtpTrustIP;

	// 已经授权验证
	private boolean authPass;

	private String missionUUID;

	private List<MailUser> rcptToList = new LinkedList<MailUser>();

	public SmtpHandleStatus(SmtpServer smtpServer) {
		this.smtpServer = smtpServer;
	}

	public int getCmdpos() {
		return cmdpos;
	}

	public void setCmdpos(int cmdpos) {
		this.cmdpos = cmdpos;
	}

	public int getHeloType() {
		return heloType;
	}

	public void setHeloType(int heloType) {
		this.heloType = heloType;
	}

	public String getHeloName() {
		return heloName;
	}

	public void setHeloName(String heloName) {
		this.heloName = heloName;
	}

	public MailUser getFromMailUser() {
		return fromMailUser;
	}

	public void setFromMailUser(MailUser fromMailUser) {
		this.fromMailUser = fromMailUser;
	}

	public MailUser getAuthMailUser() {
		return authMailUser;
	}

	public void setAuthMailUser(MailUser authMailUser) {
		this.authMailUser = authMailUser;
	}

	public boolean isSmtpTrustIP() {
		return isSmtpTrustIP;
	}

	public void setSmtpTrustIP(boolean isSmtpTrustIP) {
		this.isSmtpTrustIP = isSmtpTrustIP;
	}

	public boolean isAuthPass() {
		return authPass;
	}

	public void setAuthPass(boolean authPass) {
		this.authPass = authPass;
	}

	public String getMissionUUID() {
		return missionUUID;
	}

	public void setMissionUUID(String missionUUID) {
		this.missionUUID = missionUUID;
	}

	public void addRcptTo(MailUser mailUser) {
		rcptToList.add(mailUser);
	}

	public List<MailUser> getRcptToList() {
		return rcptToList;
	}

	public void reset() {
		cmdpos = CMDPOS_START;
		// heloType = HELLO_TYPE_NO;
		heloName = "";
		fromMailUser = null;
		// authPass = false;
		rcptToList.clear();
		rcptToList = new LinkedList<MailUser>();
	}
}