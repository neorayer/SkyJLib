package com.skymiracle.sql;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skymiracle.auth.Password;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.DaoDefine;
import com.skymiracle.mdo4.KeyNotExistException;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;


public class SQLTools {
	public static String safeSqlString(String in) {
		if (in == null)
			return "";
		String s = in.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("'", "''");
		return s;
	}

	public static Connection getConnection(String url, String username,
			String password) throws SQLException {
		Logger.detail("getConnection " + url + " " + username + " " + password);
		return DriverManager.getConnection(url, username, password);
	}

	// public static boolean releaseConn(Connection conn) {
	// if (conn == null)
	// return true;
	// try {
	// conn.close();
	// } catch (SQLException e) {
	// Logger.error("SQLTools.releaseConn()", e);
	// return false;
	// }
	// return true;
	// }
	//
	// public static boolean releaseStmt(Statement stmt) {
	// if (stmt == null)
	// return true;
	// try {
	// stmt.close();
	// } catch (SQLException e) {
	// Logger.error("SQLTools.releaseStmt()", e);
	// return false;
	// }
	// return true;
	// }
	//
	// public static boolean releaseRs(ResultSet rs) {
	// if (rs == null)
	// return true;
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// Logger.error("SQLTools.releaseStmt()", e);
	// return false;
	// }
	// return true;
	// }

	public static void exeUpdateSQL(String url, String username,
			String password, String sql) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug(sql);
			stmt.executeUpdate(sql);
			stmt.close();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} finally {
				if (conn != null)
					conn.close();
			}
		}
	}

	public static void insertFromFile(String url, String username,
			String password, File file, String tbname) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("LOAD DATA LOW_PRIORITY INFILE ").append("\"").append(
				file.getAbsolutePath()).append("\" ").append("INTO TABLE ")
				.append(tbname);
		SQLTools.exeUpdateSQL(url, username, password, sb.toString());
	}

	public static String[] getTables(String url, String username,
			String password) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getTables(null, null, "%", new String[] { "TABLE" });
			List<String> tablesList = new ArrayList<String>();
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				tablesList.add(tableName);
			}
			stmt.close();
			return tablesList.toArray(new String[0]);
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} finally {
					if (conn != null)
						conn.close();
				}
			}
		}
	}

	public static boolean isTableExist(String url, String username,
			String password, String table) throws SQLException {
		String[] tables = getTables(url, username, password);
		for (String tb : tables) {
			if (tb.equalsIgnoreCase(table))
				return true;
		}
		return false;
		// Connection conn = null;
		// ResultSet rs = null;
		// try {
		// conn = getConnection(url, username, password);
		// DatabaseMetaData metaData = conn.getMetaData();
		// rs = metaData.getTables(null,null,table,new String[]{"TABLE"});
		// if(rs.next())
		// return true;
		// else
		// return false;
		// } catch (SQLException e) {
		// throw e;
		// } finally {
		// releaseConn(conn);
		// releaseRs(rs);
		// }
	}

	public static void forceCreateTable(String url, String username,
			String password, Class daoClass, String table) throws SQLException {
		boolean isTableExist = isTableExist(url, username, password, table);
		if (isTableExist)
			return;
		createTable(url, username, password, daoClass, table);
	}

	public static void createTable(String url, String username,
			String password, Class daoClass, String table) throws SQLException {
		String sql = getCreateTableSQL(daoClass, table);
		exeUpdateSQL(url, username, password, sql);
	}

	public static void dropTable(String url, String username, String password,
			String table) throws SQLException {
		String sql = "DROP TABLE " + table;
		exeUpdateSQL(url, username, password, sql);
	}

	public static String getCreateTableSQL(Class<? extends Dao> daoClass, String table) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ").append(table).append(" (");
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
		StringBuffer primaryKeySb = new StringBuffer();
		for (BeanAttr attr: attrs) {
			Method getterMethod = attr.getMethod;
			sb.append(attr.fieldname).append(" ");

			// sb.append("`").append(attrs[i].fieldname).append("` ");
			if (attr.type == String.class) {
				int len = 255;
				try {
					Field field = attr.field;
					if (field.isAnnotationPresent(DaoDefine.class)) {
						DaoDefine def = field.getAnnotation(DaoDefine.class);
						len = def.len();
					}
				} catch (Exception e) {
					Logger.error("fieldname=" +attr.fieldname + " daoClass=" + daoClass.getName(), e);
				}

				sb.append(" varchar(" + len + ") ");
			} else if (getterMethod.getReturnType() == short.class)
				sb.append("bigint ");
			else if (getterMethod.getReturnType() == int.class)
				sb.append("bigint ");
			else if (getterMethod.getReturnType() == long.class)
				sb.append("bigint ");
			else if (getterMethod.getReturnType() == float.class)
				sb.append("float ");
			else if (getterMethod.getReturnType() == double.class)
				sb.append("double ");
			else if (getterMethod.getReturnType() == boolean.class)
				sb.append("bool ");
			else if (getterMethod.getReturnType() == String[].class)
				sb.append(" varchar(255) ");
			else if (getterMethod.getReturnType() == StringBuffer.class)
				sb.append("longtext ");
			
			sb.append(",");
			
			if (attr.isPrimary)
				primaryKeySb.append(attr.fieldname + ",");
		}
		
		if(!"".equals(primaryKeySb.toString()))
			sb.append("primary key(" + primaryKeySb.deleteCharAt(primaryKeySb.length() - 1) + ")");
		else
			sb.deleteCharAt(sb.length() - 1);
		
		sb.append(")");
		return sb.toString();	
	}

	public static void insertDao(String url, String username, String password,
			Dao dao, String table) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, SQLException,
			NullKeyException {
		insertDao(url, username, password, new Dao[] { dao }, table);
	}

	public static void insertDao(String url, String username, String password,
			Dao[] daos, String table) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, SQLException,
			NullKeyException {
		StringBuffer sql = SQLTools.getInsertSQL(daos, table);
		SQLTools.exeUpdateSQL(url, username, password, sql.toString());
	}

	public static List getDaos(String url, String username, String password,
			Class daoClass, DaoAttrSet daoAttrSet, String table)
			throws IllegalArgumentException, SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return getDaos(url, username, password, daoClass, daoAttrSet, table,
				"1=1");
	}

	public static List getDaos(String url, String username, String password,
			Class daoClass, DaoAttrSet daoAttrSet, String table, String filter)
			throws IllegalArgumentException, SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return getDaos(url, username, password, daoClass, daoAttrSet, table,
				filter, null, true);
	}

	public static StringBuffer getSQL(Map map) {
		StringBuffer sb = new StringBuffer("1=1");
		if (map == null)
			return sb;
		Set keySet = map.keySet();
		Iterator iter = keySet.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = map.get(key);
			if (value != null)
				sb.append(" AND ").append(key).append("=").append(
						getSQLSafeValue(value, value.getClass()));
		}
		return sb;
	}

	public static void loadDao(String url, String username, String password,
			Dao dao, String table) throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			KeyNotExistException, NullKeyException, DaoBuildException {
		String sql = "SELECT * FROM " + table + " WHERE " + dao.keySQL();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug(sql);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(dao.getClass());
			if (rs.next()) {
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class type = attrs[i].type;
					if (type == String[].class) {
						String field = rs.getString(attrs[i].fieldname);
						if (field.equals(""))
							arg = new String[0];
						else
							arg = field.split("::::");
					} else if (type == String.class)
						arg = rs.getString(attrs[i].fieldname);
					else if (type == long.class)
						arg = new Long(rs.getLong(attrs[i].fieldname));
					else if (type == int.class)
						arg = new Integer(rs.getInt(attrs[i].fieldname));
					else if (type == short.class)
						arg = new Short(rs.getShort(attrs[i].fieldname));
					else if (type == byte.class)
						arg = new Byte(rs.getByte(attrs[i].fieldname));
					else if (type == double.class)
						arg = new Double(rs.getDouble(attrs[i].fieldname));
					else if (type == float.class)
						arg = new Float(rs.getFloat(attrs[i].fieldname));
					else if (type == boolean.class)
						arg = new Boolean(rs.getBoolean(attrs[i].fieldname));
					else if (type == StringBuffer.class)
						arg = new StringBuffer(rs.getString(attrs[i].fieldname));
					attrs[i].setMethod.invoke(dao, new Object[] { arg });
				}

			} else {
				throw new KeyNotExistException(sql);
			}
		} catch (SQLException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} finally {
					if (conn != null)
						conn.close();
				}
			}
		}
	}

	public static long count(String url, String username, String password,
			DaoAttrSet daoAttrSet, String table, String filter)
			throws SQLException {
		long count = 0;
		StringBuffer sqlSb = new StringBuffer("SELECT COUNT(*) FROM ").append(
				table).append(" WHERE ");
		if (filter != null)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(daoAttrSet));
		String sql = sqlSb.toString();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				count = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} finally {
					if (conn != null)
						conn.close();
				}
			}
		}
		return count;
	}

	public static List getDaos(String url, String username, String password,
			Class daoClass, DaoAttrSet daoAttrSet, String table, String filter,
			String orderBy, boolean isASC) throws SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return getDaos(url, username, password, daoClass, daoAttrSet, table,
				filter, orderBy, isASC, -1, -1);
	}

	public static List getDaosBySQL(String url, String username,
			String password, Class daoClass, String sql) throws SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		List daoList = new ArrayList();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug(sql);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
			while (rs.next()) {
				Dao newDao = (Dao) daoClass.newInstance();
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class type = attrs[i].type;
					if (type == String[].class)
						arg = rs.getString(attrs[i].fieldname).split("::::");
					else if (type == String.class)
						arg = rs.getString(attrs[i].fieldname);
					else if (type == StringBuffer.class)
						arg = new StringBuffer(rs.getString(attrs[i].fieldname));
					else if (type == long.class)
						arg = new Long(rs.getLong(attrs[i].fieldname));
					else if (type == int.class)
						arg = new Integer(rs.getInt(attrs[i].fieldname));
					else if (type == short.class)
						arg = new Short(rs.getShort(attrs[i].fieldname));
					else if (type == byte.class)
						arg = new Byte(rs.getByte(attrs[i].fieldname));
					else if (type == double.class)
						arg = new Double(rs.getDouble(attrs[i].fieldname));
					else if (type == float.class)
						arg = new Float(rs.getFloat(attrs[i].fieldname));
					else if (type == boolean.class)
						arg = new Boolean(rs.getBoolean(attrs[i].fieldname));
					attrs[i].setMethod.invoke(newDao, new Object[] { arg });
				}
				daoList.add(newDao);
			}
		} catch (SQLException e) {
			throw e;
		} catch (InstantiationException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} finally {
					if (conn != null)
						conn.close();
				}
			}
		}
		return daoList;
	}

	public static List getDaos(String url, String username, String password,
			Class daoClass, DaoAttrSet daoAttrSet, String table, String filter,
			String orderBy, boolean isASC, long limitBegin, long limitCount)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		List daoList = new ArrayList();
		StringBuffer sqlSb = new StringBuffer("SELECT * FROM ").append(table)
				.append(" WHERE ");
		if (filter != null)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(daoAttrSet));
		if (orderBy != null)
			sqlSb.append(" ORDER BY ").append(orderBy).append(
					isASC ? " ASC" : " DESC");
		if (limitBegin >= 0)
			sqlSb.append(" LIMIT " + limitBegin + ", " + limitCount);
		String sql = sqlSb.toString();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(url, username, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug(sql);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
			while (rs.next()) {
				Dao newDao = (Dao) daoClass.newInstance();
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class type = attrs[i].type;
					if (type == String[].class)
						arg = rs.getString(attrs[i].fieldname).split("::::");
					else if (type == String.class)
						arg = rs.getString(attrs[i].fieldname);
					else if (type == StringBuffer.class)
						arg = new StringBuffer(rs.getString(attrs[i].fieldname));
					else if (type == long.class)
						arg = new Long(rs.getLong(attrs[i].fieldname));
					else if (type == int.class)
						arg = new Integer(rs.getInt(attrs[i].fieldname));
					else if (type == short.class)
						arg = new Short(rs.getShort(attrs[i].fieldname));
					else if (type == byte.class)
						arg = new Byte(rs.getByte(attrs[i].fieldname));
					else if (type == double.class)
						arg = new Double(rs.getDouble(attrs[i].fieldname));
					else if (type == float.class)
						arg = new Float(rs.getFloat(attrs[i].fieldname));
					else if (type == boolean.class)
						arg = new Boolean(rs.getBoolean(attrs[i].fieldname));
					attrs[i].setMethod.invoke(newDao, new Object[] { arg });
				}
				daoList.add(newDao);
			}
		} catch (SQLException e) {
			throw e;
		} catch (InstantiationException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} finally {
					if (conn != null)
						conn.close();
				}
			}
		}
		return daoList;
	}

	public static String getSQLSafeValue(Object value, Class type) {
		if (value == null)
			return "''";
		if (type == String.class)
			return "'" + safeSqlString(value.toString()) + "'";
		else if (type == StringBuffer.class)
			return "'" + safeSqlString(value.toString()) + "'";
		else if (type == boolean.class)
			return value.toString().equals("true") ? "1" : "0";
		else if (type == String[].class) {
			StringBuffer sb = new StringBuffer();
			String[] vs = (String[]) value;
			sb.append("'");
			for (int vi = 0; vi < vs.length; vi++) {
				sb.append(safeSqlString(vs[vi]));
				sb.append("::::");
			}
			sb.append("'");
			return sb.toString();
		}
		return value.toString();
	}

	public static StringBuffer getInsertSQL(Dao[] daos, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NullKeyException {
		if (daos.length == 0)
			return new StringBuffer();

		Class daoClass = daos[0].getClass();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);

		StringBuffer sb = new StringBuffer();

		for (int di = 0; di < daos.length; di++) {
			sb.append("INSERT INTO ").append(table).append("(");
			for (int i = 0; i < attrs.length; i++) {
				sb.append(attrs[i].fieldname);
				// sb.append('`').append(attrs[i].fieldname).append('`');

				if (i < attrs.length - 1) {
					sb.append(",");
				}
			}
			sb.append(") VALUES (");
			for (int i = 0; i < attrs.length; i++) {
				Object value = attrs[i].getMethod.invoke(daos[di],
						new Object[0]);
				if (value == null) {
					if (daos[di].isKeyField(attrs[i].fieldname))
						throw new NullKeyException(attrs[i].fieldname);
				}

				sb.append(getSQLSafeValue(value, attrs[i].type));

				// The last one
				if (i < attrs.length - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
			if (di < daos.length - 1)
				sb.append(";\r\n");
		}
		return sb;
	}

	public static void modifyDao(String url, String username, String password,
			Dao[] daos, DaoAttrSet[] daoAttrSets, String table)
			throws SQLException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			DaoBuildException, NullKeyException {
		StringBuffer sql = getModifySQL(daos, daoAttrSets, table);
		SQLTools.exeUpdateSQL(url, username, password, sql.toString());
	}

	public static void modifyDao(String url, String username, String password,
			Class daoClass, DaoAttrSet dataDaoAttrSet, DaoAttrSet condition,
			String table) throws SQLException {
		StringBuffer sql = getModifySQL(daoClass, dataDaoAttrSet, condition,
				table);
		SQLTools.exeUpdateSQL(url, username, password, sql.toString());

	}

	private static StringBuffer getModifySQL(Class daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition, String table) {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);

		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE ").append(table).append(" SET ");
		boolean hasOne = false;
		for (int i = 0; i < attrs.length; i++) {
			Object value = dataDaoAttrSet.get(attrs[i].fieldname);
			if (value == null)
				continue;

			if (hasOne)
				sb.append(",");
			sb.append(attrs[i].fieldname).append("=");
			sb.append(getSQLSafeValue(value, attrs[i].type));
			hasOne = true;
		}

		sb.append(" WHERE ").append(getSQL(condition));
		return sb;
	}

	private static StringBuffer getModifySQL(Dao[] daos,
			DaoAttrSet[] daoAttrSets, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, DaoBuildException, NullKeyException {
		if (daos.length == 0)
			return new StringBuffer();

		Class daoClass = daos[0].getClass();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);

		StringBuffer sb = new StringBuffer();

		for (int di = 0; di < daos.length; di++) {
			sb.append("UPDATE ").append(table).append(" SET ");
			boolean hasOne = false;
			for (int i = 0; i < attrs.length; i++) {
				Object value = daoAttrSets[di].get(attrs[i].fieldname);
				if (value == null)
					continue;

				if (hasOne)
					sb.append(",");
				sb.append(attrs[i].fieldname).append("=");
				sb.append(getSQLSafeValue(value, attrs[i].type));
				hasOne = true;
			}

			sb.append(" WHERE ").append(daos[di].keySQL());
			if (di < daos.length - 1)
				sb.append(";\r\n");
		}
		return sb;
	}

	public static boolean auth(String url, String username, String password,
			String table, Dao dao, String passwordFieldname, String inPassword)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			KeyNotExistException, NullKeyException, DaoBuildException {
		SQLTools.loadDao(url, username, password, dao, table);
		String pwdsrc = (String) dao.fieldValue(passwordFieldname);
		Password Opassword = new Password(inPassword);
		return Opassword.check(pwdsrc);
	}

	/**
	 * 废弃，绝对禁止调用此方法
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param tablename
	 * @param columns
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	@Deprecated 
	public static ResultSet exeQueryByFilter(String url, String username,
			String password, String tablename, String[] columns, String filter)
			throws SQLException {
		Connection conn = null;
		Statement state = null;
		StringBuffer sbsql = new StringBuffer();
		String column = "";
		if (columns != null) {
			for (int i = 0; i < columns.length; i++) {
				column += columns[i] + ",";
			}
			column = column.substring(0, column.lastIndexOf(","));
		} else
			column = "*";
		sbsql.append("SELECT ").append(column).append(" FROM ").append(
				tablename);
		if (filter != null)
			sbsql.append(" WHERE ").append(filter);
		conn = getConnection(url, username, password);
		state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		Logger.info(sbsql.toString());
		return state.executeQuery(sbsql.toString());
	}

	public static void incDao(String url, String username, String password,
			Class<? extends Dao> daoClass,DaoAttrSet condition, String table, String fieldName, int value) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ").append(table).append(" SET ");
		sb.append(fieldName ).append("=").append(fieldName).append("+").append(""+value);
				sb.append(" WHERE ").append(getSQL(condition));
				
		exeUpdateSQL(url, username, password, sb.toString());
	}

}
