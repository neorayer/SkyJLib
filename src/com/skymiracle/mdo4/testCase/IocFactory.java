package com.skymiracle.mdo4.testCase;

import java.net.MalformedURLException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.mdo4.confDao.*;

public class IocFactory {
	private static BeanFactory beanFactory = null;

	public static BeanFactory getBeanFactory() throws MalformedURLException,
			ClassNotFoundException {
		if (beanFactory == null) {
			String path =  IocFactory.class.getPackage().getName().replace('.','/') +"/spring-default.xml";
			System.out.println(path);
			Resource resource = new ClassPathResource(path);
			beanFactory = new XmlBeanFactory(resource);
			RdbmsConf rdbmsConf = (RdbmsConf) beanFactory.getBean("PortalRdbmsConf");
			Class.forName(rdbmsConf.getJdbcDriver());
			
		}
		return beanFactory;
	}
}
