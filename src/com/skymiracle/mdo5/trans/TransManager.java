package com.skymiracle.mdo5.trans;

import java.sql.SQLException;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.*;

public class TransManager {

	private static ThreadLocal<Trans> threadLocal = new ThreadLocal<Trans>();

	public static boolean isTransed() {
		Trans trans = threadLocal.get();
		return trans != null;
	}

	public static RdbmsSession getSqlSession(int rdsHashCode)
			throws NoTransException {
		Trans trans = threadLocal.get();
		if (trans == null) {
			throw new NoTransException();
		}
		return trans.getSqlSession(rdsHashCode);
	}

	public static void registerRdbmsSession(int rdsHashCode, RdbmsSession session)
			throws NoTransException {
		Trans trans = threadLocal.get();
		if (trans == null)
			throw new NoTransException();
		trans.putSqlSession(rdsHashCode, session);
	}

	public static void commit() throws SQLException, NoTransException {
		Trans trans = threadLocal.get();

		if (trans == null)
			throw new NoTransException();

		log("Commit and Remove");
		trans.commit();
		threadLocal.remove();
	}

	public static void begin() {
		Trans trans = threadLocal.get();
		if (trans == null) {
			trans = new Trans();
			threadLocal.set(trans);
		}

	}

	public static void log(String msg) {
		Logger.detail("TransManager: " + Thread.currentThread().getId() + " : "
				+ msg);
	}

	public static void rollback() throws NoTransException, SQLException {
		Trans trans = threadLocal.get();

		if (trans == null)
			return;

		log("Rollback");
		trans.rollback();
		log("Remove");
		threadLocal.remove();
	}

}
