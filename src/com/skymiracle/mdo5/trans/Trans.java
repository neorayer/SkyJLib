package com.skymiracle.mdo5.trans;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.skymiracle.mdo5.*;


	public class Trans {
		private Map<Integer, RdbmsSession> sessionMap = new HashMap<Integer, RdbmsSession>();

		public Trans() {
			TransManager.log("Create a Trans");
		}

		protected void commit() throws SQLException {
			for (RdbmsSession session : sessionMap.values()) {
				session.commitAndClose();
			}

		}
		
		public RdbmsSession getSqlSession(int rdsHashCode) {
			TransManager.log("Get a RdbmsSession for " + rdsHashCode);
			return sessionMap.get(rdsHashCode);
		}
		
		public void putSqlSession(int rdsHashCode, RdbmsSession session) {
			TransManager.log("Register a RdbmsSession for " + rdsHashCode);
			sessionMap.put(rdsHashCode, session);
		}

		public void rollback() throws SQLException {
			for (RdbmsSession session : sessionMap.values()) {
				session.rollback();
			}
		}

	}