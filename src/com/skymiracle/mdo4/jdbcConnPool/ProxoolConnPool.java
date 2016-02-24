package com.skymiracle.mdo4.jdbcConnPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.DaoStorageException;

public class ProxoolConnPool extends JdbcConnPool {

	private String alias;

	private String propsPath;

	private boolean isInited = false;

	@Override
	public void init() throws DaoStorageException {
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			if (alias == null)
				throw new DaoStorageException("ProxoolConnPool.alias = null");
			if (propsPath == null)
				throw new DaoStorageException(
						"ProxoolConnPool.propsPath = null");

			initPropsPath();
			PropertyConfigurator.configure(propsPath);
		} catch (Exception e) {
			throw new DaoStorageException(e);
		}
		this.isInited = true;
	}

	private void initPropsPath() throws IOException {
		File file = new File(propsPath);
		if (file.exists())
			return;
		
		file.getParentFile().mkdirs();

		// INIT create proxool properties file
		Properties props = new Properties();
		props.setProperty("jdbc-"+alias+".proxool.alias", alias);
		props.setProperty("jdbc-"+alias+".proxool.driver-url", rdbmsConf.getUrl());
		props.setProperty("jdbc-"+alias+".proxool.driver-class", rdbmsConf
				.getJdbcDriver());
		props.setProperty("jdbc-"+alias+".user", rdbmsConf.getUsername());
		props.setProperty("jdbc-"+alias+".password", rdbmsConf.getPassword());
		props.setProperty("jdbc-"+alias+".proxool.maximum-connection-count", "200");
		props.setProperty("jdbc-"+alias+".proxool.house-keeping-test-sql",
				"select CURRENT_DATE");

		Logger.info("Init Create proxool props file: " + this.propsPath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			props.store(fos, "");
		} finally {
			if (fos != null)
				fos.close();
		}
	}

	@Override
	public Connection getConnection() throws DaoStorageException {
		if (!isInited)
			init();
		if (this.rdbmsConf == null)
			throw new DaoStorageException(
					"ProxoolConnPool alias can not be null");
		try {
			Logger.detail("get connection from ProxoolConnPool");
			return DriverManager.getConnection("proxool." + this.alias);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPropsPath() {
		return propsPath;
	}

	public void setPropsPath(String propsPath) {
		this.propsPath = propsPath;
	}

}
