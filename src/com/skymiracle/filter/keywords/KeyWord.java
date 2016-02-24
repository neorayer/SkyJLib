package com.skymiracle.filter.keywords;

public class KeyWord {
	// subject | body | att | textatt | mailfrom |
	// mailto | mailcc | deliver | deposit | bounce |

	private String name;
	private boolean isSubject;
	private boolean isBody;
	private boolean isAtt;
	private boolean isTextAtt;
	private boolean isMailFrom;
	private boolean isMailto;
	private boolean isMailcc;
	private boolean isDeliver;
	private boolean isDeposit;
	private boolean isBounce;

	public boolean isDeliver() {
		return this.isDeliver;
	}

	public boolean isAtt() {
		return this.isAtt;
	}

	public boolean isBody() {
		return this.isBody;
	}

	public boolean isBounce() {
		return this.isBounce;
	}

	public boolean isDeposit() {
		return this.isDeposit;
	}

	public boolean isMailcc() {
		return this.isMailcc;
	}

	public boolean isMailFrom() {
		return this.isMailFrom;
	}

	public boolean isMailto() {
		return this.isMailto;
	}

	public boolean isSubject() {
		return this.isSubject;
	}

	public boolean isTextAtt() {
		return this.isTextAtt;
	}

	public String getName() {
		return this.name;
	}

	public void setDeliver(boolean isDeliver) {
		this.isDeliver = isDeliver;
	}

	public void setAtt(boolean isAtt) {
		this.isAtt = isAtt;
	}

	public void setBody(boolean isBody) {
		this.isBody = isBody;
	}

	public void setBounce(boolean isBounce) {
		this.isBounce = isBounce;
	}

	public void setDeposit(boolean isDeposit) {
		this.isDeposit = isDeposit;
	}

	public void setMailcc(boolean isMailcc) {
		this.isMailcc = isMailcc;
	}

	public void setMailFrom(boolean isMailFrom) {
		this.isMailFrom = isMailFrom;
	}

	public void setMailto(boolean isMailto) {
		this.isMailto = isMailto;
	}

	public void setSubject(boolean isSubject) {
		this.isSubject = isSubject;
	}

	public void setTextAtt(boolean isTextAtt) {
		this.isTextAtt = isTextAtt;
	}

	public void setName(String name) {
		this.name = name;
	}
}
