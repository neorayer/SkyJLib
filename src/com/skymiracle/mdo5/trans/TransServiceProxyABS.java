package com.skymiracle.mdo5.trans;

import java.lang.reflect.Method;
import java.sql.SQLException;

public class TransServiceProxyABS {

	protected void currentInvoke(Method method) {
		TransManager.log("Invoke " + method.toGenericString());
	}

	protected void beforeInvoke(Method method) {
		if(isNeedTrans(method)) {
			TransManager.log("");
			TransManager.log("Before method ::: <<<<");
			TransManager.begin();
		}
	}

	protected void afterInvoke(Method method) throws SQLException, NoTransException {
		if(isNeedTrans(method)) {
			TransManager.commit();
			TransManager.log("After method ::: >>>>");
		}
	}
	

	protected void rollback() throws NoTransException, SQLException {
		TransManager.rollback();
		TransManager.log("After method ::: Rollback >>>>");
	}
	
	protected boolean isNeedTrans(Method method) {
		if(method.isAnnotationPresent(TransDefine.class)){
			TransDefine transDefine = method.getAnnotation(TransDefine.class);
			if(transDefine.useTrans())
				return true;
		}
		return false;
	}
}
