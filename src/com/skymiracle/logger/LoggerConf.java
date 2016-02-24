package com.skymiracle.logger;

public class LoggerConf {

	private String level = "DEBUG";

	private String simpleFilePath = "/var/log/wpx.log";

	private String target = "STDOUT";

	private boolean isHideSQL = false;

	public LoggerConf() {
		super();
	}

	public LoggerConf(String level, String simpleFilePath, String target) {
		super();
		this.level = level;
		this.simpleFilePath = simpleFilePath;
		this.target = target;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSimpleFilePath() {
		return this.simpleFilePath;
	}

	public void setSimpleFilePath(String simpleFilePath) {
		this.simpleFilePath = simpleFilePath;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean getIsHideSQL() {
		return isHideSQL;
	}

	public void setIsHideSQL(boolean isHideSQL) {
		this.isHideSQL = isHideSQL;
	}


}
