package com.skymiracle.mdo4.trans;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TransServiceProxyCGLIB extends TransServiceProxyABS implements
		MethodInterceptor, TransServiceProxy {

	private Object target;

	@SuppressWarnings("unchecked")
	public void setTarget(Object target) {
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public Object getTargetProxied() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		enhancer.setCallbacks(new Callback[] { this });
		return  enhancer.create();
	}

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		beforeInvoke(method);
		currentInvoke(method);
		try {
			Object proxyObj = proxy.invoke(this.target, args);
			afterInvoke(method);
			return proxyObj;
		} catch (Exception e) {
			rollback();
			throw e;
		}
	}


}
