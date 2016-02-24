package com.skymiracle.mdo5;

import java.sql.SQLException;
import java.util.Collections;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.jdbcPool.*;
import com.skymiracle.mdo5.trans.*;
import com.skymiracle.mdo5.cache.*;
import com.skymiracle.sor.exception.AppException;

public class RdbmsStore implements Store {

	private JdbcPool jdbcPool = new NoJdbcPool();

	private MdoCache mdoCache = null;

	public RdbmsStore() {
		super();
	}

	public boolean auth(Mdo<?> mdo, String passwordFieldname, String password)
			throws AppException, Exception {
		if (password == null)
			throw new AppException("密码不能为空");
		try {
			return session().auth(table(mdo.getClass()), mdo,
					passwordFieldname, password);
		} catch (NotExistException e) {
			return false;
		}
	}

	public long count(Class<? extends Mdo<?>> mdoClass) throws AppException,
			Exception {
		return count(mdoClass, null);
	}

	public long count(Class<? extends Mdo<?>> mdoClass, MdoMap mdoMap)
			throws AppException, Exception {
		return count(mdoClass, mdoMap, null);
	}

	public long count(Class<? extends Mdo<?>> mdoClass, MdoMap mdoMap,
			String filter) throws AppException, Exception {
		return session().count(mdoMap, table(mdoClass), filter);
	}

	public long count(Class<? extends Mdo<?>> mdoClass, String fieldsFormat,
			Object... values) throws AppException, Exception {
		MdoMap mdoMap = new MdoMap().filledBy(fieldsFormat, values);
		return count(mdoClass, mdoMap);
	}

	@SuppressWarnings("unchecked")
	public <T extends Mdo<T>> T create(T mdo) throws AppException, Exception,
			NullKeyException {
		create(new Mdo[] { mdo });
		return mdo;
	}

	public <T extends Mdo<T>> void create(T[] mdos) throws AppException,
			Exception, NullKeyException {
		if (mdos.length == 0)
			return;
		for (T mdo : mdos) {
			mdo.filledByAutoCreate();
		}
		session().create(mdos, table(mdos[0].getClass()));
	}

	public <T extends Mdo<T>> T createOrUpdate(T mdo) throws AppException,
			Exception {
		return exists(mdo) ? update(mdo, mdo.toMdoMap()) : create(mdo);
	}

	public <T extends Mdo<T>> void createTable(Class<T> mdoClass)
			throws AppException, Exception {
		session().createTable(mdoClass, table(mdoClass));
		Logger.info("table " + table(mdoClass) + " is created.");
	}

	public <T extends Mdo<T>> void createTableForce(Class<T> mdoClass)
			throws AppException, Exception {
		createTableForce(mdoClass, false);
	}

	public <T extends Mdo<T>> void createTableForce(Class<T> mdoClass,
			boolean isDeleteOld) throws AppException, Exception {
		if (session().isTableExist(table(mdoClass)))
			if (isDeleteOld)
				dropTable(mdoClass);
			else
				return;

		createTable(mdoClass);
	}

	public <T extends Mdo<T>> void createTableForce(Class<T> mdoClass,
			String table, boolean isDeleteOld) throws AppException, Exception {
		if (session().isTableExist(table))
			if (isDeleteOld) {
				if (mdoCache != null) {
					mdoCache.remove(session().findId(mdoClass, null, table,
							null, null, true, -1, 0));
				}
				session().dropTable(table);
			} else
				return;

		session().createTable(mdoClass, table);
	}

	public <T extends Mdo<T>> void delete(Class<T> mdoClass, MdoMap mdoMap)
			throws AppException, Exception, NullKeyException, NotEmptyException {
		if (mdoCache != null) {
			mdoCache.remove(session().findId(mdoClass, mdoMap, table(mdoClass),
					null, null, true, -1, 0));
		}

		String sql = "DELETE FROM  " + table(mdoClass) + " WHERE "
				+ RdbmsSession.getSQL(mdoMap, true);
		session().exeUpdateSQL(sql);
	}

	public <T extends Mdo<T>> void delete(MList<T> mdos) throws AppException,
			Exception, NullKeyException {
		if (mdos.size() == 0)
			return;

		// Remove from cache
		if (mdoCache != null)
			mdoCache.remove(mdos);

		StringBuffer sb = new StringBuffer("DELETE FROM  "
				+ table(mdos.get(0).getClass()) + " WHERE ");
		int i = 0;
		int size = mdos.size();
		for (T mdo : mdos) {
			sb.append("(" + mdo.keySQL() + ")");
			if (i++ < size - 1)
				sb.append(" or ");
		}
		session().exeUpdateSQL(sb.toString());
	}

	@SuppressWarnings("unchecked")
	public <T extends Mdo<T>> void delete(T mdo) throws AppException, Exception {
		delete(new Mdo[] { mdo });
	}

	public <T extends Mdo<T>> void delete(T[] mdos) throws AppException,
			Exception {
		MList<T> mdosList = new MList<T>();
		Collections.addAll(mdosList, mdos);
		delete(mdosList);
	}

	public <T extends Mdo<T>> void deleteAll(Class<T> mdoClass)
			throws AppException, Exception {
		// Remove from Cache
		if (mdoCache != null)
			mdoCache.remove(session().findId(mdoClass, null, table(mdoClass),
					null, null, false, -1, 0));

		StringBuffer sb = new StringBuffer("DELETE FROM  " + table(mdoClass)
				+ " WHERE 1=1");
		session().exeUpdateSQL(sb.toString());

	}

	public <T extends Mdo<T>> void dropTable(Class<T> mdoClass)
			throws AppException, Exception {
		if (session().isTableExist(table(mdoClass))) {
			if (mdoCache != null)
				mdoCache.remove(session().findId(mdoClass, null,
						table(mdoClass), null, null, true, -1, 0));
		}
		session().dropTable(table(mdoClass));
	}

	/**
	 * 
	 * Modified by zhourui 2009/07/29
	 */
	public <T extends Mdo<T>> boolean exists(T mdo) throws AppException,
			Exception, NullKeyException, MdoBuildException {
		// 当有cache配置，并且能在cache中发现时，表示存在，返回true
		if (mdoCache != null) {
			if (mdoCache.exists(mdo)) {
				return true;
			}
		}
		return count(mdo.mdoClass(), mdo.toKeyMdoMap()) > 0;
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap)
			throws AppException, Exception {
		return find(mdoClass, mdoMap, null);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter) throws AppException, Exception {
		return find(mdoClass, mdoMap, filter, null, true);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String orderBy, boolean isASC) throws AppException, Exception {
		return find(mdoClass, mdoMap, null, orderBy, isASC);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter, String orderBy, boolean isASC) throws AppException,
			Exception {
		return find(mdoClass, mdoMap, filter, orderBy, isASC, -1, 0);
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter, String orderBy, boolean isASC, long limitBegin,
			long limitCount) throws AppException, Exception {
		if (mdoCache == null)
			return session().find(mdoClass, mdoMap, table(mdoClass), filter,
					orderBy, isASC, limitBegin, limitCount);

		return session().findId(mdoClass, mdoMap, table(mdoClass), filter,
				orderBy, isASC, limitBegin, limitCount).load();
	}

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass,
			String fieldsFormat, Object... values) throws AppException,
			Exception {
		MdoMap mdoMap = new MdoMap().filledBy(fieldsFormat, values);
		return find(mdoClass, mdoMap);
	}

	public <T extends Mdo<T>> MList<T> findAll(Class<T> mdoClass)
			throws AppException, Exception {
		return find(mdoClass, null);
	}

	public <T extends Mdo<T>> MList<T> findBySQL(Class<T> mdoClass, String sql)
			throws AppException, Exception {
		return session().findBySQL(mdoClass, sql);
	}

	public <T extends Mdo<T>> String getCreateTableSQL(Class<T> mdoClass)
			throws AppException, Exception, SQLException {
		return session().getCreateTableSQL(mdoClass, table(mdoClass));
	}

	public JdbcPool getJdbcPool() {
		return jdbcPool;
	}

	public MdoCache getMdoCache() {
		return this.mdoCache;
	}

	public void inc(Mdo<?> mdo, String fieldName, int value)
			throws AppException, Exception {
		session().inc(mdo.mdoClass(), mdo.toKeyMdoMap(), table(mdo.mdoClass()),
				fieldName, value);
	}

	public <T extends Mdo<T>> T load(T mdo) throws AppException, Exception,
			NullKeyException, NotExistException {
		if (mdoCache != null) {
			if (!mdoCache.load(mdo)) {
				session().load(mdo, table((mdo.getClass())));
				mdoCache.put(mdo);
			}
			return mdo;
		}

		session().load(mdo, table((mdo.getClass())));
		return mdo;
	}

	private RdbmsSession session() throws AppException, Exception {
		if (TransManager.isTransed()) {
			RdbmsSession session = TransManager.getSqlSession(this.hashCode());
			if (session == null) {
				session = new RdbmsSession(jdbcPool.getConnection(), false);
				TransManager.registerRdbmsSession(this.hashCode(), session);
			}
			return session;
		} else {
			RdbmsSession session = new RdbmsSession(jdbcPool.getConnection(),
					true);
			return session;
		}
	}

	public void setJdbcPool(JdbcPool jdbcPool) {
		this.jdbcPool = jdbcPool;
	}

	public void setMdoCache(MdoCache mdoCache) {
		this.mdoCache = mdoCache;
		this.mdoCache.initialize();
	}

	public <T extends Mdo<T>> long sum(Class<T> mdoClass, String fieldName,
			MdoMap mdoMap, String filter) throws AppException, Exception {
		return session().sum(fieldName, mdoMap, table(mdoClass), filter);
	}

	private String table(Class<?> mdoClass) throws AppException, Exception {
		Mdo<?> mdo = (Mdo<?>) mdoClass.newInstance();
		if (mdo.table() != null)
			return mdo.table();
		String tableName = mdoClass.getName().replace('.', '_');
		return tableName;
	}

	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			MdoMap condition) throws AppException, Exception, NullKeyException,
			NotExistException {
		// Remove from cache
		if (mdoCache != null)
			mdoCache.remove(session().findId(mdoClass, condition,
					table(mdoClass), null, null, true, -1, 0));

		dataMdoMap.filledByAutoModify(mdoClass);
		session().update(mdoClass, dataMdoMap, condition, table(mdoClass));
	}

	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			StringBuffer conditionSQL) throws AppException, Exception,
			NullKeyException, NotExistException {
		// Remove from cache
		if (mdoCache != null)
			mdoCache.remove(session().findId(mdoClass, conditionSQL.toString(),
					table(mdoClass)));

		dataMdoMap.filledByAutoModify(mdoClass);
		session().update(mdoClass, dataMdoMap, conditionSQL, table(mdoClass));
	}

	public <T extends Mdo<T>> T update(T mdo, MdoMap mdoMap)
			throws AppException, Exception, NullKeyException, MdoBuildException {
		update(mdo.mdoClass(), mdoMap, mdo.toKeyMdoMap());
		return mdo;
	}

	public <T extends Mdo<T>> T update(T mdo, String fieldsFormat,
			Object... values) throws AppException, Exception {
		MdoMap mdoMap = new MdoMap().filledBy(fieldsFormat, values);
		return update(mdo, mdoMap);
	}

}
