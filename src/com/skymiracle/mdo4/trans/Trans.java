package com.skymiracle.mdo4.trans;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.skymiracle.sql.SQLSession;


	public class Trans {
		private Map<Integer, SQLSession> sqlSessionMap = new HashMap<Integer, SQLSession>();

		public Trans() {
			TransManager.log("Create a Trans");
		}

		protected void commit() throws SQLException {
			for (SQLSession sqlSession : sqlSessionMap.values()) {
				sqlSession.commitAndClose();
			}

		}
		
		public SQLSession getSqlSession(int rdsHashCode) {
			TransManager.log("Get a SQLSession for " + rdsHashCode);
			return sqlSessionMap.get(rdsHashCode);
		}
		
		public void putSqlSession(int rdsHashCode, SQLSession sqlSession) {
			TransManager.log("Register a SQLSession for " + rdsHashCode);
			sqlSessionMap.put(rdsHashCode, sqlSession);
		}

		public void rollback() throws SQLException {
			for (SQLSession sqlSession : sqlSessionMap.values()) {
				sqlSession.rollback();
			}
		}

	}