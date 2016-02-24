package com.skymiracle.mdo4.jdbcConnPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.DaoStorageException;

public class NoPoolJdbcConnPool extends JdbcConnPool {

	@Override
	public Connection getConnection() throws DaoStorageException {
		Logger.detail("NoPoolJdbcConnPool: Create new Connection from " + rdbmsConf.getUrl());
		Connection conn;
		try {
			conn = DriverManager.getConnection(rdbmsConf.getUrl(), rdbmsConf
					.getUsername(), rdbmsConf.getPassword());
			return conn;
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	};
}
