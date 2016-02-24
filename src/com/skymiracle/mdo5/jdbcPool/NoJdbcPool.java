package com.skymiracle.mdo5.jdbcPool;

import java.sql.Connection;
import java.sql.DriverManager;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;

public class NoJdbcPool extends JdbcPool {

	@Override
	public Connection _getConnection() throws AppException, Exception {
		Logger.detail("NoPoolJdbcConnPool: Create new Connection from " + url);
		return DriverManager.getConnection(url, username, password);
	}

	@Override
	protected void init() throws AppException, Exception {
		Class.forName(jdbcDriver).newInstance();
	}

}
