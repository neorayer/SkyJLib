package com.skymiracle.mdo4.testCase;

import java.net.MalformedURLException;

import org.springframework.beans.BeansException;

import com.skymiracle.mdo4.trans.TransServiceProxy;
import com.skymiracle.mdo4.trans.TransServiceProxyNONE;


public class ServiceFactory {


	public static Class<? extends TransServiceProxy> transServiceProxyClass = TransServiceProxyNONE.class; 
//	public static Class<? extends TransServiceProxy> transServiceProxyClass = TransServiceProxyJP.class; 
//	public static Class<? extends TransServiceProxy> transServiceProxyClass = TransServiceProxyCGLIB.class; 
	
	private static Object getProxiedService(Object target) throws InstantiationException, IllegalAccessException {
		TransServiceProxy proxy = transServiceProxyClass.newInstance();
		proxy.setTarget(target);
		return  proxy.getTargetProxied();
		
	}
	
	public static ICaseService getCaseService() throws BeansException,
			MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		CaseService service = (CaseService) IocFactory.getBeanFactory()
				.getBean("CaseService");
		return (ICaseService) getProxiedService(service);
	}
}
