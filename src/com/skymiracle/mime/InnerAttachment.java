package com.skymiracle.mime;

public class InnerAttachment {

	private String cid;
	private String filename;
	private String filepath;

	public InnerAttachment(String path, String cid) {
		this.cid = cid;
		this.filepath = path;
		this.filename = path.substring(path.lastIndexOf("/") + 1);
	}

	public String getFilename() {
		return this.filename;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getCid() {
		return this.cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

}
