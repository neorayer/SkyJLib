package com.skymiracle.mime;

import com.skymiracle.util.Rfc2047Codec;

public class RichMailAddress {
	private String src;

	private String mailAddress = null;

	private String displayName = null;

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public RichMailAddress() {
		
	}
	/**
	 * get the mailaddress and receiver's name
	 * 
	 * @param src
	 */
	public RichMailAddress(String src) {
		if (src == null)
			return;
		this.src = src.trim();
		int pos1B = this.src.lastIndexOf('<');
		int pos1E = this.src.lastIndexOf('>');
		int pos2B = this.src.indexOf('"');
		int pos2E = this.src.lastIndexOf('"');
		// get the mail address
		if (pos1B >= 0 && pos1E >= 0 && pos1B < pos1E)
			this.mailAddress = this.src.substring(pos1B + 1, pos1E)
					.toLowerCase();
		// get the name
		if (pos2B >= 0 && pos2E >= 0 && pos2B < pos2E) {
			this.displayName = this.src.substring(pos2B + 1, pos2E);
			this.displayName = Rfc2047Codec.decode(this.displayName);
		}
		if (this.displayName == null && this.mailAddress != null)
			this.displayName = this.mailAddress;

		if (this.displayName != null && this.mailAddress == null)
			this.mailAddress = this.displayName.toLowerCase();

		if (this.displayName == null && this.mailAddress == null) {
			this.displayName = this.src.toLowerCase();
			this.mailAddress = this.src.toLowerCase();
		}
		
		if (this.displayName.trim().length() == 0) {
			this.displayName = this.mailAddress;
		}
	}

	public RichMailAddress(String name, String email) {
		this.displayName = name;
		this.mailAddress = email;
		this.src = new StringBuffer("\"").append(this.displayName).append(
				"\" <").append(this.mailAddress).append(">").toString();
	}

	public String getMailAddress() {
		return this.mailAddress;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getSrc() {
		return this.src;
	}

	@Override
	public String toString() {
		return new StringBuffer("\"").append(this.displayName).append("\" <")
				.append(this.mailAddress).append(">").toString();
	}

	public static void main(String[] args) {
		RichMailAddress r = new RichMailAddress("\"��<\">��\"<disller@tom.com>");
		System.out.println(r.displayName);
		System.out.println("\"��\"��\"<disller@tom.com>".lastIndexOf('"'));
	}
}
