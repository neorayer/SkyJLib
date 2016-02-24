package com.skymiracle.fax;

import java.io.File;

public class SourceFileNameInfo {
	private String ufaxUserID;

	private String[] targetNumbers;

	private String filename;

	public SourceFileNameInfo(File file) throws FilenameException {
		String[] ss = file.getName().split("\\[f\\[");
		if (ss.length < 3) {
			throw new FilenameException(file);
		}
		this.filename = ss[ss.length - 1];
		this.ufaxUserID = ss[ss.length - 2];
		this.targetNumbers = new String[ss.length - 2];
		for (int i = 0; i < ss.length - 2; i++) {
			this.targetNumbers[i] = ss[i];
		}
	}

	public SourceFileNameInfo() {
		this.ufaxUserID = "";
		this.targetNumbers = new String[0];
		this.filename = "";
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String[] getTargetNumbers() {
		return targetNumbers;
	}

	public String getTargetNumbersStr() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.targetNumbers.length; i++)
			sb.append(this.targetNumbers[i]).append(" ");
		return sb.toString();
	}

	public void setTargetNumbers(String[] targetNumbers) {
		this.targetNumbers = targetNumbers;
	}

	public String getUfaxUserID() {
		return ufaxUserID;
	}

	public void setUfaxUserID(String ufaxUserID) {
		this.ufaxUserID = ufaxUserID;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<this.targetNumbers.length; i++)
			sb.append(this.targetNumbers[i]).append("[f[");
		sb.append(this.ufaxUserID).append("[f[");
		sb.append(this.filename);
		return sb.toString();
	}
}
