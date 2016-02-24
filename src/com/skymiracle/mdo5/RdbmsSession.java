package com.skymiracle.mdo5;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.skymiracle.auth.Password;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.MdoReflector.MdoField;

public class RdbmsSession {

	private Connection conn;

	private boolean isAutoClose = false;

	public static enum DBType {
		ORACLE, MSSQL, MYSQL, HSQL
	}

	private static DBType curDBType;

	public RdbmsSession(Connection conn, boolean isAutoClose)
			throws SQLException {
		super();
		this.conn = conn;
		this.isAutoClose = isAutoClose;
		this.conn.setAutoCommit(isAutoClose);
		
		String type = conn.getMetaData().getDriverName().toLowerCase();
		Logger.detail("DB TYPE: " + type);
		if (type.indexOf("hsql") >= 0)
			curDBType = DBType.HSQL;
		if (type.indexOf("sql server") >= 0)
			curDBType = DBType.MSSQL;
	}

	private void logSQL(String s) {
		if (Logger.getIsHideSQL())
			return;
		Logger.debug("[SQL] " + s);
	}
	
	public boolean isMSSQL() {
		return curDBType == DBType.MSSQL;
	}

	public boolean isHSQL() {
		return curDBType == DBType.HSQL;
	}

	// public static StringBuffer getSQL(Map<String, Object> map) {
	// StringBuffer sb = new StringBuffer("1=1");
	// if (map == null)
	// return sb;
	// for (Map.Entry<String, Object> entry : map.entrySet()) {
	// String key = entry.getKey();
	// Object value = entry.getValue();
	// if (value != null)
	// sb.append(" AND ").append(key).append("=").append(
	// getSQLSafeValue(value, value.getClass()));
	// }
	//
	// return sb;
	// }

	public static StringBuffer getSQL(MdoMap map) {
		return getSQL(map, false);

	}

	public static StringBuffer getSQL(MdoMap map, boolean ignoreOrder) {
		StringBuffer sb = new StringBuffer("1=1");
		if (map == null)
			return sb;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value != null)
				sb.append(" AND ").append(key).append("=").append(
						getSQLSafeValue(value, value.getClass()));
		}

		if (!ignoreOrder)
			if (!"".equals(map.orderBy.toString()))
				sb.append(" ORDER BY " + map.orderBy.toString());

		if (map.limitCount > 0) {
			sb.append(" LIMIT " + map.limitBegin + ", " + map.limitCount);
		}

		return sb;
	}

	public static String getSQLSafeValue(Object value,
			Class<? extends Object> type) {
		if (value == null)
			return "''";
		if (type == String.class)
			return "'" + safeSqlString(value.toString()) + "'";
		else if (type.isEnum())
			return "'" + safeSqlString(value.toString()) + "'";
		else if (type == StringBuffer.class)
			return "'" + safeSqlString(value.toString()) + "'";
		else if (type == boolean.class || type == Boolean.class)
			return value.toString().equalsIgnoreCase("true") ? "1" : "0";
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

	public static String safeSqlString(String in) {
		if (in == null)
			return "";
		String s = in.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("'", "''");
		return s;
	}

	public boolean auth(String table, Mdo<?> mdo, String passwordFieldname,
			String inPassword) throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NotExistException, NullKeyException, MdoBuildException {
		load(mdo, table);
		String pwdsrc = (String) mdo.fieldValue(passwordFieldname);
		Password Opassword = new Password(inPassword);
		return Opassword.check(pwdsrc);
	}

	private void close() throws SQLException {
		if (!this.isAutoClose)
			return;
		if (conn != null) {
			//Logger.detail("SQL: Close a connection");
			conn.close();
			conn = null;
		}
	}

	public void closeFinal() throws SQLException {
		if (conn != null) {
			//Logger.detail("SQL: Close a connection final");
			conn.close();
			conn = null;
		}
	}

	public void commit() throws SQLException {
		this.conn.commit();
	}

	public void commitAndClose() throws SQLException {
		try {
			commit();
		} finally {
			closeFinal();
		}
	}

	public long count(MdoMap mdoMap, String table, String filter)
			throws SQLException {
		long count = 0;
		StringBuffer sqlSb = new StringBuffer("SELECT COUNT(*) FROM ").append(
				table).append(" WHERE ");
		if (filter != null && filter.length() > 0)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(mdoMap, true));
		String sql = sqlSb.toString();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
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
					close();
				}
			}
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public <T extends Mdo<T>> void create(T mdo, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, NullKeyException {
		create(new Mdo[] { mdo }, table);
	}

	public <T extends Mdo<T>> void create(T[] mdos, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, NullKeyException {
		StringBuffer sql = getInsertSQL(mdos, table);
		exeUpdateSQL(sql.toString());
	}

	public void createFromFile(File file, String tbname) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("LOAD DATA LOW_PRIORITY INFILE ").append("\"").append(
				file.getAbsolutePath()).append("\" ").append("INTO TABLE ")
				.append(tbname);
		exeUpdateSQL(sb.toString());
	}

	public <T extends Mdo<T>> void createTable(Class<T> mdoClass, String table)
			throws SQLException {
		String sql = getCreateTableSQL(mdoClass, table);
		exeUpdateSQL(sql);
	}

	public void dropTable(String table) throws SQLException {
		String sql = "DROP TABLE " + table;
		exeUpdateSQL(sql);
	}

	public void exeUpdateSQL(String sql) throws SQLException {

		Statement stmt = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			stmt.executeUpdate(sql);
			stmt.close();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} finally {
				close();
			}
		}
	}
	
	public <T extends Mdo<T>> MList<T> find(boolean keyOnly, Class<T> mdoClass, String table, String filter)
			throws SQLException {
		MList<T> mdoList = new MList<T>();
		String fields = "*";
		MdoField[] mdoFields = MdoReflector.getMdoFields(mdoClass);
		if (keyOnly) {
			String[] keyNames = Mdo.instance(mdoClass).keyNames();
			StringBuffer sb = new StringBuffer();
			for (String keyName : keyNames) {
				sb.append(keyName);
				sb.append(",");
			}
			fields = sb.substring(0, sb.length() - 1);

			mdoFields = MdoReflector.getMdoKeyFields(mdoClass);
		}
		
		// filter 没有做安全SQL处理
		StringBuffer sqlSb = new StringBuffer("SELECT ").append(fields).append(
		" FROM ").append(table).append(" WHERE ").append(filter);
		
		String sql = sqlSb.toString();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T newMdo = rsToMdo(mdoClass, mdoFields, rs);
				mdoList.add(newMdo);
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
					close();

				}
			}
		}
		return mdoList;
	}

	public <T extends Mdo<T>> MList<T> find(boolean keyOnly, Class<T> mdoClass,
			MdoMap mdoMap, String table, String filter, String orderBy,
			boolean isASC, long limitBegin, long limitCount)
			throws SQLException {
		MList<T> mdoList = new MList<T>();
		String fields = "*";
		MdoField[] mdoFields = MdoReflector.getMdoFields(mdoClass);
		if (keyOnly) {
			String[] keyNames = Mdo.instance(mdoClass).keyNames();
			StringBuffer sb = new StringBuffer();
			for (String keyName : keyNames) {
				sb.append(keyName);
				sb.append(",");
			}
			fields = sb.substring(0, sb.length() - 1);

			mdoFields = MdoReflector.getMdoKeyFields(mdoClass);
		}
		
		if (limitCount > 0) {
			mdoMap.limitBegin = (int)limitBegin;
			mdoMap.limitCount = (int)limitCount;
		}
		
		if (mdoMap != null && mdoMap.limitCount > 0) {
			if (isMSSQL()) {
				int begin = mdoMap.limitBegin;
				int count = mdoMap.limitCount;
				mdoMap.limitBegin = mdoMap.limitCount = -1;
				MList<T> mdos = find(keyOnly, mdoClass, mdoMap, table, filter, orderBy, isASC, -1, -1);
				return mdos.sub(begin, count);
			}
		}
		
		if (mdoMap != null && orderBy !=null && !"".equals(orderBy)) {
			if (!"".equals(mdoMap.orderBy.toString())) 
				mdoMap.orderBy.append(",");
			mdoMap.orderBy.append(orderBy).append(
					isASC ? " ASC" : " DESC");
		}
		
		StringBuffer sqlSb = new StringBuffer("SELECT ").append(fields).append(
				" FROM ").append(table).append(" WHERE ");
		{
			sqlSb.append(getSQL(mdoMap));
			if (filter != null && filter.length() > 0) {
				int orderByIndex = sqlSb.indexOf(" ORDER BY ");
				if(orderByIndex > 0) {
					sqlSb.insert(orderByIndex, " AND (" + filter + ")");
				}
				else
					sqlSb.append(" AND (").append(filter).append(")");
			}
		}
		String sql = sqlSb.toString();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T newMdo = rsToMdo(mdoClass, mdoFields, rs);
				mdoList.add(newMdo);
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
					close();

				}
			}
		}
		return mdoList;
	}

	public <T extends Mdo<T>> List<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String table) throws IllegalArgumentException, SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return find(mdoClass, mdoMap, table, "1=1");
	}

	public <T extends Mdo<T>> List<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String table, String filter) throws IllegalArgumentException,
			SQLException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return find(mdoClass, mdoMap, table, filter, null, true);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String table, String filter, String orderBy, boolean isASC)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		return find(mdoClass, mdoMap, table, filter, orderBy, isASC, -1, -1);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String table, String filter, String orderBy, boolean isASC,
			long limitBegin, long limitCount) throws SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return find(false, mdoClass, mdoMap, table, filter, orderBy, isASC,
				limitBegin, limitCount);
	}

	public <T extends Mdo<T>> MList<T> findBySQL(Class<T> mdoClass, String sql)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		MList<T> mdoList = new MList<T>();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			rs = stmt.executeQuery(sql);
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int length = rsmd.getColumnCount();
			MdoField[] attrs = MdoReflector.getMdoFields(mdoClass);
			while (rs.next()) {
				T newMdo = rsToMdo(mdoClass, attrs, rs);
				mdoList.add(newMdo);
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
					close();

				}
			}
		}
		return mdoList;
	}

	public <T extends Mdo<T>> MList<T> findId(Class<T> mdoClass, MdoMap mdoMap,
			String table, String filter, String orderBy, boolean isASC,
			long limitBegin, long limitCount) throws SQLException {
		return find(true, mdoClass, mdoMap, table, filter, orderBy, isASC,
				limitBegin, limitCount);
	}
	
	public <T extends Mdo<T>> MList<T> findId(Class<T> mdoClass,  String filter, String table) throws SQLException {
		return find(true, mdoClass, table, filter);
	}

	public <T extends Mdo<T>> void forceCreateTable(Class<T> mdoClass, String table)
			throws SQLException {
		boolean isTableExist = isTableExist(table);
		if (isTableExist)
			return;
		createTable(mdoClass, table);
	}

	public Connection getConn() {
		return conn;
	}

	public <T extends Mdo<T>> String getCreateTableSQL(Class<T> mdoClass,
			String table) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ").append(table).append(" (");
		StringBuffer primaryKeySb = new StringBuffer();
		for (MdoField mf : MdoReflector.getMdoFields(mdoClass)) {
			sb.append(mf.name).append(" ");

			if (mf.type == String.class) {
				int len = 64;
				if (mf.length > 0)
					len = mf.length;
				try {
				} catch (Exception e) {
					Logger.error("name=" + mf.name + " mdoClass="
							+ mdoClass.getName(), e);
				}

				sb.append(" varchar(" + len + ") ");
			} else if (mf.type == short.class)
				sb.append("bigint ");
			else if (mf.type == int.class)
				sb.append("bigint ");
			else if (mf.type == long.class)
				sb.append("bigint ");
			else if (mf.type == float.class)
				sb.append("float ");
			else if (mf.type == double.class)
				sb.append("double ");
			else if (mf.type == boolean.class)
				sb.append("int ");
			else if (mf.type == String[].class)
				sb.append(" varchar(255) ");
			else if (mf.type == StringBuffer.class) {
				if (isHSQL())
					sb.append("longvarchar ");
				else if(isMSSQL())
					sb.append("text ");
				else
					sb.append("longtext ");
			} else if (mf.type.isEnum()) {
				sb.append(" varchar(255) ");
			}
			sb.append(",");

			if (mf.isPrimary)
				primaryKeySb.append(mf.name + ",");
		}

		if (!"".equals(primaryKeySb.toString()))
			sb.append("primary key("
					+ primaryKeySb.deleteCharAt(primaryKeySb.length() - 1)
					+ ")");
		else
			sb.deleteCharAt(sb.length() - 1);

		sb.append(")");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <T extends Mdo<T>> StringBuffer getInsertSQL(T[] mdos, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NullKeyException {
		if (mdos.length == 0)
			return new StringBuffer();

		Class<T> mdoClass = (Class<T>) mdos[0].getClass();
		MdoField[] attrs = MdoReflector.getMdoFields(mdoClass);

		StringBuffer sb = new StringBuffer();

		for (int di = 0; di < mdos.length; di++) {
			sb.append("INSERT INTO ").append(table).append("(");
			for (int i = 0; i < attrs.length; i++) {
				sb.append(attrs[i].name);
				// sb.append('`').append(attrs[i].name).append('`');

				if (i < attrs.length - 1) {
					sb.append(",");
				}
			}
			sb.append(") VALUES (");
			for (int i = 0; i < attrs.length; i++) {
				Object value = attrs[i].getMethod.invoke(mdos[di],
						new Object[0]);
				if (value == null) {
					if (mdos[di].isKeyField(attrs[i].name))
						throw new NullKeyException(attrs[i].name);
				}

				sb.append(getSQLSafeValue(value, attrs[i].type));

				// The last one
				if (i < attrs.length - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
			if (di < mdos.length - 1)
				sb.append(";\r\n");
		}
		return sb;
	}

	private <T extends Mdo<T>> StringBuffer getModifySQL(Class<T> mdoClass,
			MdoMap dataMdoMap, MdoMap condition, String table) {
		
		
		return getModifySQL(mdoClass, dataMdoMap, getSQL(condition, true), table);
		
//		MdoField[] attrs = MdoReflector.getMdoFields(mdoClass);
//
//		StringBuffer sb = new StringBuffer();
//
//		sb.append("UPDATE ").append(table).append(" SET ");
//		boolean hasOne = false;
//		for (int i = 0; i < attrs.length; i++) {
//			Object value = dataMdoMap.get(attrs[i].name);
//			if (value == null)
//				continue;
//
//			if (hasOne)
//				sb.append(",");
//			sb.append(attrs[i].name).append("=");
//			sb.append(getSQLSafeValue(value, attrs[i].type));
//			hasOne = true;
//		}
//
//		sb.append(" WHERE ").append(getSQL(condition, true));
//		return sb;
	}
	
	private <T extends Mdo<T>> StringBuffer getModifySQL(Class<T> mdoClass,
			MdoMap dataMdoMap, StringBuffer conditionSQL, String table) {
		MdoField[] attrs = MdoReflector.getMdoFields(mdoClass);

		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE ").append(table).append(" SET ");
		boolean hasOne = false;
		for (int i = 0; i < attrs.length; i++) {
			Object value = dataMdoMap.get(attrs[i].name);
			if (value == null)
				continue;

			if (hasOne)
				sb.append(",");
			sb.append(attrs[i].name).append("=");
			sb.append(getSQLSafeValue(value, attrs[i].type));
			hasOne = true;
		}

		// // conditionSQL 没有做安全SQL处理
		sb.append(" WHERE ").append(conditionSQL);
		return sb;
	}

	@SuppressWarnings("unchecked")
	private <T extends Mdo<T>> StringBuffer getModifySQL(T[] mdos,
			MdoMap[] mdoMaps, String table) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			MdoBuildException, NullKeyException {
		if (mdos.length == 0)
			return new StringBuffer();

		Class<T> mdoClass = (Class<T>) mdos[0].getClass();
		StringBuffer sb = new StringBuffer();

		for (int di = 0; di < mdos.length; di++) {
			sb.append("UPDATE ").append(table).append(" SET ");
			boolean hasOne = false;
			for (MdoField mf : MdoReflector.getMdoFields(mdoClass)) {
				Object value = mdoMaps[di].get(mf.name);
				if (value == null)
					continue;

				if (hasOne)
					sb.append(",");
				sb.append(mf.name).append("=");
				sb.append(getSQLSafeValue(value, mf.type));
				hasOne = true;
			}

			sb.append(" WHERE ").append(mdos[di].keySQL());
			if (di < mdos.length - 1)
				sb.append(";\r\n");
		}
		return sb;
	}

	public String[] getTables(Connection conn) throws SQLException {

		Statement stmt = null;
		ResultSet rs = null;
		try {

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
					close();

				}
			}
		}
	}

	public void inc(Class<? extends Mdo<?>> mdoClass, MdoMap condition,
			String table, String fieldName, int value) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ").append(table).append(" SET ");
		sb.append(fieldName).append("=").append(fieldName).append("+").append(
				"" + value);
		sb.append(" WHERE ").append(getSQL(condition, true));

		exeUpdateSQL(sb.toString());
	}

	public boolean isTableExist(String table) throws SQLException {
		String[] tables = getTables(conn);
		for (String tb : tables) {
			if (tb.equalsIgnoreCase(table))
				return true;
		}
		return false;
		// 
		// ResultSet rs = null;
		// try {
		// 
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

	@SuppressWarnings("unchecked")
	public void load(Mdo<?> mdo, String table) throws SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NotExistException, NullKeyException,
			MdoBuildException {
		String sql = "SELECT * FROM " + table + " WHERE " + mdo.keySQL();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Object arg = null;
				for (MdoField mf : MdoReflector.get(mdo).getMdoFields()) {
					Class<?> type = mf.type;
					if (type == String[].class) {
						String field = rs.getString(mf.name);
						if (field.equals(""))
							arg = new String[0];
						else
							arg = field.split("::::");
					} else if (type == String.class)
						arg = rs.getString(mf.name);
					else if (type == long.class)
						arg = new Long(rs.getLong(mf.name));
					else if (type == int.class)
						arg = new Integer(rs.getInt(mf.name));
					else if (type == short.class)
						arg = new Short(rs.getShort(mf.name));
					else if (type == byte.class)
						arg = new Byte(rs.getByte(mf.name));
					else if (type == double.class)
						arg = new Double(rs.getDouble(mf.name));
					else if (type == float.class)
						arg = new Float(rs.getFloat(mf.name));
					else if (type == boolean.class)
						arg = new Boolean(rs.getBoolean(mf.name));
					else if (type == StringBuffer.class)
						arg = new StringBuffer(rs.getString(mf.name));
					else if (type.isEnum()) {
						arg = Enum.valueOf((Class<Enum>) type, rs
								.getString(mf.name));
					}
					mf.setMethod.invoke(mdo, new Object[] { arg });
				}

			} else {
				throw new NotExistException(mdo);
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
					close();

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Mdo<T>> T rsToMdo(Class<T> mdoClass, MdoField[] mFields,
			ResultSet rs) throws SQLException {
		T mdo = Mdo.instance(mdoClass);
		Object arg = null;
		for (MdoField mf : mFields) {
			Class<? extends Object> type = mf.type;
			if (type == String[].class)
				arg = rs.getString(mf.name).split("::::");
			else if (type == String.class)
				arg = rs.getString(mf.name);
			else if (type == StringBuffer.class)
				arg = new StringBuffer(rs.getString(mf.name));
			else if (type == long.class)
				arg = new Long(rs.getLong(mf.name));
			else if (type == int.class)
				arg = new Integer(rs.getInt(mf.name));
			else if (type == short.class)
				arg = new Short(rs.getShort(mf.name));
			else if (type == byte.class)
				arg = new Byte(rs.getByte(mf.name));
			else if (type == double.class)
				arg = new Double(rs.getDouble(mf.name));
			else if (type == float.class)
				arg = new Float(rs.getFloat(mf.name));
			else if (type == boolean.class)
				arg = new Boolean(rs.getBoolean(mf.name));
			else if (type.isEnum()) {
				arg = Enum.valueOf((Class<Enum>) type, rs.getString(mf.name));
			}
			mf.setValue(mdo, arg);
		}
		return mdo;
	}

	public void rollback() throws SQLException {
		try {
			this.conn.rollback();
		} finally {
			closeFinal();
		}
	}

	public long sum(String fieldName, MdoMap mdoMap, String table, String filter)
			throws SQLException {
		long sum = 0;
		StringBuffer sqlSb = new StringBuffer("SELECT SUM(").append(fieldName)
				.append(") FROM ").append(table).append(" WHERE ");
		if (filter != null)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(mdoMap, true));
		String sql = sqlSb.toString();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			logSQL(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				sum = rs.getLong(1);
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
					close();
				}
			}
		}
		return sum;
	}

	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			MdoMap condition, String table) throws SQLException {
		StringBuffer sql = getModifySQL(mdoClass, dataMdoMap, condition, table);
		exeUpdateSQL(sql.toString());

	}
	
	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			StringBuffer conditionSQL, String table) throws SQLException {
		StringBuffer sql = getModifySQL(mdoClass, dataMdoMap, conditionSQL, table);
		exeUpdateSQL(sql.toString());
	}

	public <T extends Mdo<T>> void update(T[] mdos, MdoMap[] mdoMaps, String table)
			throws SQLException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			MdoBuildException, NullKeyException {
		StringBuffer sql = getModifySQL(mdos, mdoMaps, table);
		exeUpdateSQL(sql.toString());
	}

}
