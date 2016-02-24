package com.skymiracle.mdo5.jdbcPool;

import java.sql.Connection;
import org.apache.commons.dbcp.BasicDataSource;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;

public class DbcpJdbcPool extends JdbcPool {
	private BasicDataSource ds = new BasicDataSource();

	@Override
	protected Connection _getConnection() throws AppException, Exception {
		Logger.detail("Get Connection from DBCP");
		return ds.getConnection();

	}

	@Override
	protected void init() throws AppException, Exception {
		Class.forName(this.jdbcDriver);
		 ds = new BasicDataSource();
		ds.setDriverClassName(this.jdbcDriver);
		ds.setUsername(this.username);
		ds.setPassword(this.password);
		ds.setUrl(this.url);
	}

}
