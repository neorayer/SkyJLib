package com.skymiracle.sql;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.DList;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.DaoDefine;
import com.skymiracle.mdo4.KeyNotExistException;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;
import com.skymiracle.mdo5.MdoMap;

public class SQLSession {
	
	private Connection conn;
	
	private boolean isAutoClose = false;

	public SQLSession(Connection conn, boolean isAutoClose) throws SQLException {
		super();
		this.conn = conn;
		this.isAutoClose = isAutoClose;
		this.conn.setAutoCommit(isAutoClose);
	}
	
	public Connection getConn() {
		return conn;
	}

	private void close() throws SQLException {
		if (!this.isAutoClose)
			return;
		if (conn != null) {
			Logger.detail("SQL: Close a connection");
			conn.close();
			conn = null;
		}
	}
	
	public void closeFinal() throws SQLException {
		if (conn != null) {
			Logger.detail("SQL: Close a connection final");
			conn.close();
			conn = null;
		}
	}
	
	
	public static String safeSqlString(String in) {
		if (in == null)
			return "";
		String s = in.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("'", "''");
		return s;
	}
	public  void exeUpdateSQL( String sql)
			throws SQLException {
		
		Statement stmt = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: " + sql);
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

	public  void insertFromFile( File file,
			String tbname) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("LOAD DATA LOW_PRIORITY INFILE ").append("\"").append(
				file.getAbsolutePath()).append("\" ").append("INTO TABLE ")
				.append(tbname);
		exeUpdateSQL( sb.toString());
	}

	public  String[] getTables(Connection conn) throws SQLException {
		
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

	public  boolean isTableExist( String table)
			throws SQLException {
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

	public  void forceCreateTable( Class<? extends Dao> daoClass,
			String table) throws SQLException {
		boolean isTableExist = isTableExist( table);
		if (isTableExist)
			return;
		createTable(daoClass, table);
	}

	public  void createTable(  Class<? extends Dao> daoClass,
			String table) throws SQLException {
		String sql = getCreateTableSQL(daoClass, table);
		exeUpdateSQL(sql);
	}

	public  void dropTable( String table)
			throws SQLException {
		String sql = "DROP TABLE " + table;
		exeUpdateSQL(sql);
	}

	public  String getCreateTableSQL(Class<? extends Dao> daoClass,
			String table) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ").append(table).append(" (");
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
		StringBuffer primaryKeySb = new StringBuffer();
		for (BeanAttr attr : attrs) {
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
					Logger.error("SQL: fieldname=" + attr.fieldname + " daoClass="
							+ daoClass.getName(), e);
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

		if (!"".equals(primaryKeySb.toString()))
			sb.append("primary key("
					+ primaryKeySb.deleteCharAt(primaryKeySb.length() - 1)
					+ ")");
		else
			sb.deleteCharAt(sb.length() - 1);

		sb.append(")");
		return sb.toString();
	}

	public  void insertDao( Dao dao, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, NullKeyException {
		insertDao( new Dao[] { dao }, table);
	}

	public  void insertDao( Dao[] daos, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, NullKeyException {
		StringBuffer sql = getInsertSQL(daos, table);
		exeUpdateSQL( sql.toString());
	}

	public  <T  extends Dao>List<T> getDaos( Class<T> daoClass,
			DaoAttrSet daoAttrSet, String table)
			throws IllegalArgumentException, SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return getDaos( daoClass, daoAttrSet, table, "1=1");
	}

	public  <T extends Dao>List<T> getDaos( Class<T> daoClass,
			DaoAttrSet daoAttrSet, String table, String filter)
			throws IllegalArgumentException, SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return getDaos( daoClass, daoAttrSet, table, filter,
				null, true);
	}

	public static StringBuffer getSQL(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer("1=1");
		if (map == null)
			return sb;
		for(Map.Entry<String, Object> entry: map.entrySet()) {
			String key = entry.getKey();
			Object value =entry.getValue();
			if (value != null)
				sb.append(" AND ").append(key).append("=").append(
						getSQLSafeValue(value, value.getClass()));
		}

		return sb;
	}
	
	public static StringBuffer getSQL(MdoMap map) {
		StringBuffer sb = new StringBuffer("1=1");
		if (map == null)
			return sb;
		for(Map.Entry<String, Object> entry: map.entrySet()) {
			String key = entry.getKey();
			Object value =entry.getValue();
			if (value != null)
				sb.append(" AND ").append(key).append("=").append(
						getSQLSafeValue(value, value.getClass()));
		}
		
		if(!"".equals(map.orderBy.toString()))
			sb.append(" ORDER BY " + map.orderBy.toString());
		
		if(map.limitCount > 0) {
			sb.append(" LIMIT " + map.limitBegin + ", " + map.limitCount);
		}
		
		return sb;
	}

	public  void loadDao( Dao dao, String table)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			KeyNotExistException, NullKeyException, DaoBuildException {
		String sql = "SELECT * FROM " + table + " WHERE " + dao.keySQL();

		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: "+sql);
			rs = stmt.executeQuery(sql);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(dao.getClass());
			if (rs.next()) {
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class<? extends Object>   type = attrs[i].type;
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
					close();

				}
			}
		}
	}

	public  long count( DaoAttrSet daoAttrSet,
			String table, String filter) throws SQLException {
		long count = 0;
		StringBuffer sqlSb = new StringBuffer("SELECT COUNT(*) FROM ").append(
				table).append(" WHERE ");
		if (filter != null)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(daoAttrSet));
		String sql = sqlSb.toString();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: "+sql);
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

	public long sum(String fieldName, DaoAttrSet daoAttrSet, String table, String filter) throws SQLException {
		// TODO Auto-generated method stub
		long sum = 0;
		StringBuffer sqlSb = new StringBuffer("SELECT SUM(").append(fieldName).append(") FROM ").append(
				table).append(" WHERE ");
		if (filter != null)
			sqlSb.append(filter).append(" AND ");
		sqlSb.append(getSQL(daoAttrSet));
		String sql = sqlSb.toString();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: "+sql);
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
	
	public <T extends Dao> DList<T> getDaos( Class<T> daoClass,
			DaoAttrSet daoAttrSet, String table, String filter, String orderBy,
			boolean isASC) throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		return getDaos( daoClass, daoAttrSet, table, filter,
				orderBy, isASC, -1, -1);
	}

	public  <T  extends Dao> DList<T>  getDaosBySQL( Class<T> daoClass,
			String sql) throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		DList<T> daoList = new DList<T>();
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: "+sql);
			rs = stmt.executeQuery(sql);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
			while (rs.next()) {
				T newDao = daoClass.newInstance();
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class<? extends Object>  type = attrs[i].type;
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
					close();

				}
			}
		}
		return daoList;
	}

	public <T extends Dao> DList<T> getDaos( Class<T> daoClass,
			DaoAttrSet daoAttrSet, String table, String filter, String orderBy,
			boolean isASC, long limitBegin, long limitCount)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		DList<T> daoList = new DList<T>();
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
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Logger.debug("SQL: "+sql);
			rs = stmt.executeQuery(sql);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int length = rsmd.getColumnCount();
			BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
			while (rs.next()) {
				T newDao = daoClass.newInstance();
				Object arg = null;
				for (int i = 0; i < attrs.length; i++) {
					Class<? extends Object>  type = attrs[i].type;
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
					close();

				}
			}
		}
		return daoList;
	}

	public  static String getSQLSafeValue(Object value, Class<? extends Object> type) {
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

	public  StringBuffer getInsertSQL(Dao[] daos, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NullKeyException {
		if (daos.length == 0)
			return new StringBuffer();

		Class<? extends Dao> daoClass = daos[0].getClass();
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

	public  void modifyDao( Dao[] daos,
			DaoAttrSet[] daoAttrSets, String table) throws SQLException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, DaoBuildException, NullKeyException {
		StringBuffer sql = getModifySQL(daos, daoAttrSets, table);
		exeUpdateSQL( sql.toString());
	}

	public  void modifyDao( Class<? extends Dao> daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition, String table)
			throws SQLException {
		StringBuffer sql = getModifySQL(daoClass, dataDaoAttrSet, condition,
				table);
		exeUpdateSQL( sql.toString());

	}

	private  StringBuffer getModifySQL(Class<? extends Dao>  daoClass,
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

	private  StringBuffer getModifySQL(Dao[] daos,
			DaoAttrSet[] daoAttrSets, String table)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, DaoBuildException, NullKeyException {
		if (daos.length == 0)
			return new StringBuffer();

		Class<? extends Dao>  daoClass = daos[0].getClass();
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

	public  boolean auth( String table, Dao dao,
			String passwordFieldname, String inPassword) throws SQLException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, KeyNotExistException, NullKeyException,
			DaoBuildException {
		loadDao( dao, table);
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
	public  ResultSet exeQueryByFilter(
			String tablename, String[] columns, String filter)
			throws SQLException {
		
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
		
		state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		Logger.info("SQL: "+sbsql.toString());
		return state.executeQuery(sbsql.toString());
	}

	public  void incDao(
			Class<? extends Dao> daoClass, DaoAttrSet condition, String table,
			String fieldName, int value) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ").append(table).append(" SET ");
		sb.append(fieldName).append("=").append(fieldName).append("+").append(
				"" + value);
		sb.append(" WHERE ").append(getSQL(condition));

		exeUpdateSQL( sb.toString());
	}

	public void commit() throws SQLException {
		this.conn.commit();
	}

	public void commitAndClose() throws SQLException {
		try {
			commit();
		}finally {
			closeFinal();
		}
	}

	public void rollback() throws SQLException {
		try {
			this.conn.rollback();
		} finally {
			closeFinal();
		}
	}

}
