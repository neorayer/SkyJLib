package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;

/**
 * 本地存储访问接口
 */
public class IOAccessorLocal {

	// 用户名
	protected String username;

	// 邮件域
	protected String domain;

	// 邮件存储主目录
	protected String homePath;

	// 临时文件目录
	protected String tmpDirPath = "/tmp";

	// 文件目录设置
	protected HomeDirConfiger homeDirConfiger;

	public IOAccessorLocal(String username, String domain, String contextName,
			String rootPath, HomeDirConfiger homeDirConfiger, String tmpDirPath)
			throws IOException {
		this.username = username.toLowerCase();
		this.domain = domain.toLowerCase();
		this.homeDirConfiger = homeDirConfiger;
		this.homePath = this.homeDirConfiger.getHomeDirPath(rootPath,
				this.username, this.domain, true)
				+ "/" + contextName + "/";
		File file = new File(this.homePath);
		if (!file.exists())
			file.mkdirs();
		this.tmpDirPath = tmpDirPath;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName());
		sb.append(" username=").append(this.username);
		sb.append(" domain=").append(this.domain);
		sb.append(" tmpDirPath=").append(this.tmpDirPath);
		sb.append(" homeDirConfger=").append(this.homeDirConfiger);
		sb.append(" homePath=").append(this.homePath);
		return sb.toString();
	}

}
