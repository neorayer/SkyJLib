package com.skymiracle.mdo4.confDao;


public class XmlConf extends ConfDao {

	private String filePath = "/tmp/xmlMstorage.xml";

	private String rootXPath = "//root";

	public XmlConf() {
		super();
	}

	public XmlConf(String confID) {
		super(confID);
	}

	public XmlConf(String filePath, String rootXPath) {
		super();
		this.filePath = filePath;
		this.rootXPath = rootXPath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRootXPath() {
		return this.rootXPath;
	}

	public void setRootXPath(String rootXPath) {
		this.rootXPath = rootXPath;
	}

}
