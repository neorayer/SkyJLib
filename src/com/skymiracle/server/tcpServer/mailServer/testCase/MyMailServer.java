package com.skymiracle.server.tcpServer.mailServer.testCase;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.CmdStorageServer;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3Server;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpServer;

public class MyMailServer {

	private Pop3Server pop3Server;
	private SmtpServer smtpServer;
	private CmdStorageServer storageServer;

	private BeanFactory beanFactory;

	public MyMailServer() {
		Logger.setLevel(Logger.LEVEL_DETAIL);

		String path = this.getClass().getPackage().getName();
		path = path.replace('.', '/') + "/spring.xml";
		Resource resource = new ClassPathResource(path);
		this.beanFactory = new XmlBeanFactory(resource);

		this.pop3Server = (Pop3Server) this.beanFactory.getBean("Pop3Server");
		this.smtpServer = (SmtpServer) this.beanFactory.getBean("SmtpServer");
		this.storageServer = (CmdStorageServer) this.beanFactory
				.getBean("StorageServer");
		this.pop3Server.start();
		this.storageServer.start();
		this.smtpServer.start();
	}

	public static void main(String[] args) {
		new MyMailServer();
	}

}
