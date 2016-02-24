package com.skymiracle.util;

public class EmailAddress {

	public class BadEmailAddressException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7315291582384053265L;

	}

	private String username;
	private String domain;

	private Object attrObj;

	public Object getAttrObj() {
		return this.attrObj;
	}

	public EmailAddress(String email) throws BadEmailAddressException {
		if (email == null)
			throw new BadEmailAddressException();

		String[] ss = email.trim().split("@");
		if (ss.length == 2) {
			setUsername(ss[0]);
			setDomain(ss[1]);
		} else {
			throw new BadEmailAddressException();
		}
	}

	public EmailAddress(String username, String domain) {
		setUsername(username);
		setDomain(domain);
	}

	public void setAttrObj(Object attrObj) {
		this.attrObj = attrObj;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain.trim().toLowerCase();
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username.trim().toLowerCase();
	}

	@Override
	public String toString() {
		return this.username + '@' + this.domain;
	}
}
