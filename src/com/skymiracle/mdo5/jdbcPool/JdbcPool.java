package com.skymiracle.mdo5.jdbcPool;

import java.sql.Connection;
import com.skymiracle.sor.exception.AppException;

public abstract class JdbcPool {
	protected String url = "jdbc:hsqldb:mem:aname";

	protected String username = "sa";

	protected String password = "";

	protected String jdbcDriver = "org.hsqldb.jdbcDriver";

	private boolean isInited = false;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	protected abstract void init() throws AppException, Exception;

	public Connection getConnection()throws AppException, Exception{
		if (!this.isInited)
			init();
		this.isInited = true;
		return _getConnection();
	}

	protected abstract Connection _getConnection() throws AppException, Exception;

}
