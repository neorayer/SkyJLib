package com.skymiracle.util;

import java.util.HashMap;

public class RFC2821Session {
	private boolean isMF;
	private String[] sourceRoutes = new String[0];
	private HashMap esmtp_param = new HashMap();
	private String address;
	private String sessionStr;

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public HashMap getEsmtp_param() {
		return this.esmtp_param;
	}

	public void setEsmtp_param(HashMap esmtp_param) {
		this.esmtp_param = esmtp_param;
	}

	public boolean isMF() {
		return this.isMF;
	}

	public void setMF(boolean isMF) {
		this.isMF = isMF;
	}

	public String getSessionStr() {
		return this.sessionStr;
	}

	public void setSessionStr(String sessionStr) {
		this.sessionStr = sessionStr;
	}

	public String[] getSourceRoutes() {
		return this.sourceRoutes;
	}

	public void setSourceRoutes(String[] sourceRoutes) {
		this.sourceRoutes = sourceRoutes;
	}

}
