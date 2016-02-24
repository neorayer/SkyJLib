package com.skymiracle.util;

public class UsernameWithDomain {
	private String src;

	private String username;

	private String domain;

	public UsernameWithDomain(String src, String defaultDomain) {
		if (src == null)
			return;
		this.src = src;
		int pos = this.src.indexOf('@');
		if (pos >= 0) {
			this.username = this.src.substring(0, pos);
			this.domain = this.src.substring(pos + 1);
		} else {
			this.username = src;
			this.domain = defaultDomain;
		}
	}

	public String getUsername() {
		return this.username;
	}

	public String getDomain() {
		return this.domain;
	}

	public String toEmail() {
		return this.username + '@' + this.domain;
	}
}
