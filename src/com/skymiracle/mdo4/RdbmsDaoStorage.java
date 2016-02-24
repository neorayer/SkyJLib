package com.skymiracle.mdo4;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.cache.DaoMemCache;
import com.skymiracle.mdo4.confDao.RdbmsConf;
import com.skymiracle.mdo4.jdbcConnPool.JdbcConnPool;
import com.skymiracle.mdo4.jdbcConnPool.NoPoolJdbcConnPool;
import com.skymiracle.mdo4.trans.NoTransException;
import com.skymiracle.mdo4.trans.TransManager;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.sql.SQLSession;

/**
 * DaoStorage的关系型数据库实现。 也是目前DaoStorage各个实现类中最完整的实现类。
 * 
 * @author skymiracle
 * 
 */
public class RdbmsDaoStorage implements DaoStorage {

	private JdbcConnPool jdbcConnPool = new NoPoolJdbcConnPool();

	private RdbmsConf rdbmsConf;

	private DaoMemCache daoMemCache =null;

	/**
	 * 定表构造
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param table
	 */
	@Deprecated
	public RdbmsDaoStorage(String url, String username, String password,
			String table) {
		this();
		RdbmsConf rdbmsConf = new RdbmsConf();
		rdbmsConf.setUrl(url);
		rdbmsConf.setUsername(username);
		rdbmsConf.setPassword(password);
	}

	/**
	 * 无参构造，配合Spring使用，味道最佳
	 * 
	 */
	public RdbmsDaoStorage() {
		super();
	}

	public void setDaoMemCache(DaoMemCache daoMemCache) {
		this.daoMemCache = daoMemCache;
		this.daoMemCache.initialize();
	}

	public void initDriver() throws DaoStorageException {
		jdbcConnPool.initDriver();
	}

	public JdbcConnPool getJdbcConnPool() {
		return jdbcConnPool;
	}

	private String getTable(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		try {
			Dao dao = daoClass.newInstance();

			if (dao.table() != null)
				return dao.table();
			String tableName = daoClass.getName().replace('.', '_');
			return tableName;
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		}
	}

	public void empty(Class<? extends Dao> daoClass) throws DaoStorageException {
		StringBuffer sb = new StringBuffer("DELETE FROM  " + getTable(daoClass)
				+ " WHERE 1=1");
		exeUpdateSQL(sb.toString());
		try {
			if(daoMemCache!=null){
			daoMemCache.remove(getSqlSession().getDaos(daoClass, null, getTable(daoClass)));
			}
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public void delDao(Dao dao) throws DaoStorageException, DaoBuildException,
			NullKeyException {
		delDao(new Dao[] { dao });
		
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass)
			throws DaoStorageException {
		return getDaos(daoClass, null);
	}

	public <T extends Dao> void delDao(T[] daos) throws DaoStorageException,
			DaoBuildException, NullKeyException {
		List<T> daosList = new LinkedList<T>();
		Collections.addAll(daosList, daos);
		delDao(daosList);
	}

	public <T extends Dao> void delDao(List<T> daos)
			throws DaoStorageException, DaoBuildException, NullKeyException {
		if (daos.size() == 0)
			return;
		StringBuffer sb = new StringBuffer("DELETE FROM  "
				+ getTable(daos.get(0).getClass()) + " WHERE ");
		int i = 0;
		int size = daos.size();
		for (T dao : daos) {
			sb.append("(" + dao.keySQL() + ")");
			if (i++ < size - 1)
				sb.append(" or ");
		}
		exeUpdateSQL(sb.toString());
        if(daoMemCache!=null){
		daoMemCache.remove(daos);
        }
	}

	private void exeUpdateSQL(String sql) throws DaoStorageException {
		try {
			getSqlSession().exeUpdateSQL(sql);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
	}

	public void loadDao(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException, KeyNotExistException {
		try {
			//String key = dao.keySQL();
			boolean cachedObject = false; 
			if(daoMemCache!=null){
				cachedObject=daoMemCache.load(dao);
			    }
			if (!cachedObject){
				getSqlSession().loadDao(dao, getTable(dao.getClass()));
				if(daoMemCache!=null){
				daoMemCache.put(dao);
				}
			}
			dao.setDaoStorage(this);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet) throws DaoStorageException {
		return getDaos(daoClass, daoAttrSet, "1=1");
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		return getDaos(daoClass, daoAttrSet, filter, null, true);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String orderBy, boolean isASC)
			throws DaoStorageException {
		return getDaos(daoClass, daoAttrSet, "1=1", orderBy, isASC);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy, boolean isASC)
			throws DaoStorageException {
		try {

			DList<T> daos = getSqlSession().getDaos(daoClass, daoAttrSet,
					getTable(daoClass), filter, orderBy, isASC);
			for (T dao : daos)
				dao.setDaoStorage(this);
			return daos;

		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy,
			boolean isASC, long limitBegin, long limitCount)
			throws DaoStorageException {
		try {
			return getSqlSession().getDaos(daoClass, daoAttrSet,
					getTable(daoClass), filter, orderBy, isASC, limitBegin,
					limitCount);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> T addDao(T dao) throws DaoStorageException,
			NullKeyException {
		addDao(new Dao[] { dao });
		return dao;
	}

	public void addDao(Dao[] daos) throws DaoStorageException, NullKeyException {
		if (daos.length == 0)
			return;
		try {
            getSqlSession().insertDao(daos, getTable(daos[0].getClass()));
            
			for (Dao dao : daos)
				dao.setDaoStorage(this);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public void incDao(Class<? extends Dao> daoClass, DaoAttrSet condition,
			String fieldName, int value) throws DaoStorageException {
		try {
			getSqlSession().incDao(daoClass, condition, getTable(daoClass),
					fieldName, value);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (DaoStorageException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> T modDao(T dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException {
		modDao(new Dao[] { dao }, new DaoAttrSet[] { daoAttrSet });
		return dao;
	}

	public void modDao(Dao[] daos, DaoAttrSet[] daoAttrSets)
			throws DaoStorageException, DaoBuildException, NullKeyException {
		// Delete dao key from daoAttrSets
		if (daos.length > 0)
			if(daoMemCache!=null){
			daoMemCache.remove(daos);
			}
		for (int i = 0; i < daoAttrSets.length; i++) {
			if (daos.length >= i)
				break;
			String[] keyNames = daos[i].keyNames();
			for (String kn : keyNames) {
				daoAttrSets[i].remove(kn);
			}

		}

		if (daos.length == 0)
			return;
		try {
			getSqlSession().modifyDao(daos, daoAttrSets,
					getTable(daos[0].getClass()));

		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public void modDao(Class<? extends Dao> daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException {
		try {
			if(daoMemCache!=null){
			daoMemCache.remove(getSqlSession().getDaos(daoClass, condition,getTable(daoClass)));
			}
			getSqlSession().modifyDao(daoClass, dataDaoAttrSet, condition,
					getTable(daoClass));
     
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public void createTableForce(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		createTableForce(daoClass, false);
	}

	public void createTableForce(Class<? extends Dao> daoClass,
			boolean isDeleteOld) throws DaoStorageException {
		try {
			if (getSqlSession().isTableExist(getTable(daoClass)))
				if (isDeleteOld)
					getSqlSession().dropTable(getTable(daoClass));
				else
					return;
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
		createTable(daoClass);
	}

	public void createTable(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		try {
			getSqlSession().createTable(daoClass, getTable(daoClass));
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
		Logger.info("table " + getTable(daoClass) + "is created.");
	}

	public String getCreateTableSQL(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		return getSqlSession().getCreateTableSQL(daoClass, getTable(daoClass));
	}

	public void dropTable(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		try {
			getSqlSession().dropTable(getTable(daoClass));
			if(daoMemCache!=null){
			daoMemCache.remove(getSqlSession().getDaos(daoClass, null, getTable(daoClass)));
			}
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public boolean existDao(Dao dao) throws DaoStorageException,
			NullKeyException, DaoBuildException {
		try {
			loadDao(dao);
		} catch (KeyNotExistException e) {
			return false;
		}
		return true;
	}

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException {
		return count(daoClass, daoAttrSet, null);
	}

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) throws DaoStorageException {
		try {
			long count = getSqlSession().count(daoAttrSet, getTable(daoClass),
						filter);
				return count;
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
	}

	public long sum(Class<? extends Dao> daoClass, String fieldName,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		try {
			return getSqlSession().sum(fieldName, daoAttrSet,
					getTable(daoClass), filter);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		}
	}

	public boolean auth(Dao dao, String passwordFieldname, String password)
			throws DaoStorageException, AppException {
		if (password == null)
			throw new AppException("密码不能为空");
		try {
			return getSqlSession().auth(getTable(dao.getClass()), dao,
					passwordFieldname, password);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		} catch (DaoBuildException e) {
			throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (KeyNotExistException e) {
			return false;
		} catch (NullKeyException e) {
			throw new DaoStorageException(e);
		}
	}

	public void delDao(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			NotEmptyException {

		StringBuffer sb = new StringBuffer("DELETE FROM  " + getTable(daoClass)
				+ " WHERE ");
		sb.append(SQLSession.getSQL(daoAttrSet));
		exeUpdateSQL(sb.toString());
		try {
			// clean cache for daoClass
			if(daoMemCache!=null){
			daoMemCache.remove(getSqlSession().getDaos(daoClass,daoAttrSet,getTable(daoClass)));
			}
		} catch (IllegalArgumentException e) {
		    throw new DaoStorageException(e);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaosBySQL(Class<T> daoClass, String sql)
			throws DaoStorageException {
		try {

			return getSqlSession().getDaosBySQL(daoClass, sql);
		} catch (SQLException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	private SQLSession getSqlSession() throws DaoStorageException {
		try {
			if (TransManager.isTransed()) {
				SQLSession sqlSession = TransManager.getSqlSession(this
						.hashCode());
				if (sqlSession == null) {
					sqlSession = new SQLSession(jdbcConnPool.getConnection(),
							false);
					TransManager.registerSQLSession(this.hashCode(), sqlSession);
				}
				return sqlSession;
			} else {
				SQLSession sqlSession = new SQLSession(jdbcConnPool
						.getConnection(), true);
				return sqlSession;
			}

		} catch (SQLException e) {
			Logger.error("", e);
			throw new DaoStorageException(e);
		} catch (NoTransException e) {
			throw new DaoStorageException(e);
		}
	}

	public void setJdbcConnPool(JdbcConnPool jdbcConnPool) {
		this.jdbcConnPool = jdbcConnPool;
	}

	public RdbmsConf getRdbmsConf() {
		return rdbmsConf;
	}

	public void setRdbmsConf(RdbmsConf rdbmsConf) {
		this.rdbmsConf = rdbmsConf;
		this.jdbcConnPool.setRdbmsConf(rdbmsConf);
	}

	public DaoMemCache getDaoMemCache() {
		return this.daoMemCache;
	}

	public <T extends Dao> DList<T> getDaosByField(Class<T> daoClass,
			String field, String value) throws DaoStorageException {
		DaoAttrSet das = new DaoAttrSet();
		das.put(field, value);
		return getDaos(daoClass, das);
	}

	public <T extends Dao> T getDaoByField(Class<T> daoClass, String field,
			String value) throws DaoStorageException {
		List<T> daos = getDaosByField(daoClass, field, value);
		return daos.size() > 0 ? daos.get(0) : null;
	}

}
