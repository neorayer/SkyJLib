package com.skymiracle.mdo5.jdbcPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;

public class ProxoolJdbcPool extends JdbcPool {

	private String alias;

	private String propsPath;

	@Override
	protected void init() throws AppException, Exception {
		Class.forName(jdbcDriver).newInstance();
		Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
		if (alias == null)
			throw new Exception("ProxoolConnPool.alias = null");
		if (propsPath == null)
			throw new Exception("ProxoolConnPool.propsPath = null");

		initPropsPath();
		PropertyConfigurator.configure(propsPath);
	}

	private void initPropsPath() throws IOException {
		File file = new File(propsPath);
		// if (file.exists())
		// return;

		file.getParentFile().mkdirs();

		// INIT create proxool properties file
		Properties props = new Properties();
		props.setProperty("jdbc-" + alias + ".proxool.alias", alias);
		props.setProperty("jdbc-" + alias + ".proxool.driver-url", url);
		props
				.setProperty("jdbc-" + alias + ".proxool.driver-class",
						jdbcDriver);
		props.setProperty("jdbc-" + alias + ".user", username);
		props.setProperty("jdbc-" + alias + ".password", password);
		props.setProperty(
				"jdbc-" + alias + ".proxool.maximum-connection-count", "200");
		props.setProperty("jdbc-" + alias + ".proxool.house-keeping-test-sql",
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
	public Connection _getConnection() throws AppException, Exception {
		Logger.detail("get connection from ProxoolConnPool");
		return DriverManager.getConnection("proxool." + this.alias);
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
