package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.auth.Authable;

public class Demo {

	public static void main(String[] args) throws Exception {
		String path = Demo.class.getPackage().getName();
		path = path.replace('.', '/') + "/spring.xml";
		Resource resource = new ClassPathResource(path);
		BeanFactory beanFactory = new XmlBeanFactory(resource);
		StorageAccessorFactory saf = (StorageAccessorFactory) beanFactory
				.getBean("StorageAccessorFactory");
		IDocAccessor usa = saf.getUserStorageDocAccessor("test",
				"test.com", Authable.LOCATION_NATIVE_LOCAL);
		System.out.println(usa);
		// usa.docNewFolder("/", "abccc");
		usa.docDelFolder("/abccc");
	}
}
