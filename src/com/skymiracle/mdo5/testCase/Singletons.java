package com.skymiracle.mdo5.testCase;

import java.io.File;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.mdo5.RdbmsStore;

public class Singletons {

	private static XmlBeanFactory beanFactory = null;

	public static Mdo5User_X Mdo5UserX;

	public static RdbmsStore store;

	static {
		String path = Singletons.class.getPackage().getName().replace('.', '/')
				+ "/spring.xml";
		Resource resource = new ClassPathResource(path);
		beanFactory = new XmlBeanFactory(resource);

		store = (RdbmsStore) beanFactory.getBean("Store");

		Mdo5UserX = new Mdo5User_X(store);

		Mdo5UserX.initFullTextSearcher(new File("/tmp/mdo5UsersIdx/"));

	}
}
