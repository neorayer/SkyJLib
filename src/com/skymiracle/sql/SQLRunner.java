package com.skymiracle.sql;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class SQLRunner {

	private static String DEFAULT_CHARSET = "UTF-8";

	private static void errorOut(String msg) {
		System.out.println(msg);
		System.exit(0);
	}

	public static Properties getProperties(String filePath) throws IOException {
		String propsFilePath = filePath;
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propsFilePath);
			props.load(fis);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}
		}
		return props;
	}

	public static String loadStringFile(String filePath, String charset)
			throws Exception {
		FileInputStream fis = new FileInputStream(filePath);
		BufferedInputStream bis = new BufferedInputStream(fis, 1024);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		byte[] bytes = new byte[1024];
		int readSize = -1;
		while ((readSize = bis.read(bytes)) >= 0) {
			bos.write(bytes, 0, readSize);
		}
		bos.flush();
		bos.close();
		bis.close();

		return new String(bos.toByteArray(), charset);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			errorOut("Usage java com.skymiracle.sqlRunner.SQLRunner jdbcPropFile SQLFilePath [charset].");
		}

		String charset = DEFAULT_CHARSET;
		if (args.length == 3)
			charset = args[3];

		String propFilePath = args[0];
		String sqlFilePath = args[1];

		Properties jdbcProps = getProperties(propFilePath);
		System.out.println(jdbcProps);

		String url = jdbcProps.getProperty("url");
		String driver = jdbcProps.getProperty("driver");
		String username = jdbcProps.getProperty("username");
		String password = jdbcProps.getProperty("password");

		String sql = loadStringFile(sqlFilePath, charset);

		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		Statement stat = conn.createStatement();

		stat.execute(sql);
		stat.close();
		conn.close();
		System.out.println("1 +OK!");

	}

}
