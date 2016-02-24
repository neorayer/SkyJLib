package com.skymiracle.mdo4.confDao;

import com.skymiracle.logger.Logger;

public class RdbmsConf extends ConfDao {

	private String url = "jdbc:hsqldb:mem:aname";

	private String username = "sa";

	private String password = "";

	private String jdbcDriver = "org.hsqldb.jdbcDriver";

	private boolean isInited = false;

	public void init() {
		if (isInited)
			return;
		try {
			Class.forName(jdbcDriver).newInstance();

		} catch (InstantiationException e) {
			Logger.warn("", e);
		} catch (IllegalAccessException e) {
			Logger.warn("", e);
		} catch (ClassNotFoundException e) {
			Logger.warn("", e);
		}
		this.isInited = true;
	}


	public String getJdbcDriver() {
		return this.jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
