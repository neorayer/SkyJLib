package com.skymiracle.server.tcpServer.cmdStorageServer.testCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.server.tcpServer.cmdStorageServer.CmdStorageServer;

public class Demo {

	public static void main(String[] args) {
		String path = Demo.class.getPackage().getName();
		path = path.replace('.', '/') + "/spring.xml";
		Resource resource = new ClassPathResource(path);
		BeanFactory beanFactory = new XmlBeanFactory(resource);
		CmdStorageServer storageServer = (CmdStorageServer) beanFactory
				.getBean("StorageServer");
		storageServer.start();
	}
}
