package com.skymiracle.mime;

import com.skymiracle.util.ViewTools;

public class Attachment {

	private String filePath;

	private String fileName;

	private long fileSize;

	private String contentType;

	public Attachment() {

	}

	public Attachment(String filePath, String fileName, long fileSize,
			String contentType) {
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.contentType = contentType;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public String getFileName() {
		return this.fileName;
	}

	public long getSize() {
		return this.fileSize;
	}

	public String getContentType() {
		return this.contentType;
	}
	
	public String getTgmkSize() {
		return ViewTools.getTgmkSize(getSize());
	}
	
	public String getShortPath() {
		return this.filePath.substring(this.filePath.lastIndexOf("/"));
	}

}
