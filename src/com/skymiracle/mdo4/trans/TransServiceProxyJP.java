package com.skymiracle.mdo4.trans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransServiceProxyJP extends TransServiceProxyABS implements
		InvocationHandler, TransServiceProxy {

	private Object target;

	@SuppressWarnings("unchecked")
	public void setTarget(Object target) {
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public Object getTargetProxied() {
		Object o = Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(), this);

		return o;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		beforeInvoke(method);
		currentInvoke(method);
		try {
			Object returnedResult = method.invoke(target, args);
			afterInvoke(method);
			return returnedResult;
		} catch (Exception e) {
			rollback();
			throw e.getCause();
		}
	}

}
