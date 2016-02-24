package com.skymiracle.server.tcpServer.mailServer.wp8;

import java.io.FileInputStream;

import java.io.IOException;

import java.util.Properties;

import com.skymiracle.system.OS;
import com.skymiracle.util.UsernameWithDomain;

public class Uindex {

	private String username;

	private String domain;

	private String uIndexPath;

	private String passwordEncrypted;

	private String home;
	private int status;

	public Uindex(String username, String domain) {
		this.username = username.toLowerCase();
		this.domain = domain.toLowerCase();
		this.uIndexPath = generateUindexPath();
	}

	
	public Uindex(String domain) {
		this.username = "";
		this.domain = domain;
		this.uIndexPath = generateUindexPath();
	}

	public void load() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(this.uIndexPath));
		this.passwordEncrypted = properties.getProperty("pass");
		this.home = properties.getProperty("home");
		this.status = Integer.parseInt(properties.getProperty("status"));
	}

	public String getPasswordEncrypted() {
		return this.passwordEncrypted;
	}

	public String getHome() {
		return this.home;
	}
	public int getStatus() {
		return this.status;
	}

	private String generateUindexPath() {
		StringBuffer sb = new StringBuffer();
		if (OS.getType() == OS.TYPE_UNIX)
			sb.append("/wdpost/");
		else
			sb.append("D:\\wdpost\\");
		if (this.username.length() < 1) {
			sb.append("uindex/").append(this.domain).append(
					"/127.0.0.1/userattr.conf");
		} else {
			String s1 = this.username.substring(0, 1);
			String s2;
			if (this.username.length() == 1)
				s2 = s1;
			else
				s2 = this.username.substring(0, 2);
			sb.append("uindex/").append(this.domain).append("/127.0.0.1/")
					.append(s1).append("/").append(s2).append("/").append(
							this.username).append("/userattr.conf");
		}
		return sb.toString();

	}


	public UsernameWithDomain getUsernameWithDomain(String defaultDomain) {
		UsernameWithDomain uwd = new UsernameWithDomain(this.username + '@' + this.domain, defaultDomain);
		return uwd;
	}

}
