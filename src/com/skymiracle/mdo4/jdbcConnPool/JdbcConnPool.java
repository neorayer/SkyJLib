package com.skymiracle.mdo4.jdbcConnPool;

import java.sql.Connection;
import com.skymiracle.mdo4.DaoStorageException;
import com.skymiracle.mdo4.confDao.RdbmsConf;

public abstract class JdbcConnPool {
	protected RdbmsConf rdbmsConf = new RdbmsConf();

	public RdbmsConf getRdbmsConf() {
		return rdbmsConf;
	}

	public void setRdbmsConf(RdbmsConf rdbmsConf) {
		this.rdbmsConf = rdbmsConf;
	}

	public void initDriver() throws DaoStorageException{
		try {
			Class.forName(rdbmsConf.getJdbcDriver()).newInstance();
			init();
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (ClassNotFoundException e) {
			throw new DaoStorageException(e);
		}
		
	}

	public abstract Connection getConnection() throws  DaoStorageException;

	public abstract void init() throws DaoStorageException ;

}
