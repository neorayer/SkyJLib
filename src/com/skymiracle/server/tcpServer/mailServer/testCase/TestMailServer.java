package com.skymiracle.server.tcpServer.mailServer.testCase;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.client.tcpClient.pop3Client.Pop3Client;
import com.skymiracle.client.tcpClient.smtpClient.SmtpClient;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.CmdStorageServer;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3Server;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpServer;

public class TestMailServer extends TestCase {

	private Pop3Server pop3Server;

	private SmtpServer smtpServer;

	private CmdStorageServer storageServer;

	private BeanFactory beanFactory;

	public TestMailServer() {
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

	public void testSmtp() throws Exception {
		for (int i = 0; i < 10; i++) {
			SmtpClient smtpClient = (SmtpClient) this.beanFactory
					.getBean("LocalSmtpClient");
			smtpClient.setFromEmail("test@out.com");

			ArrayList<String> dataLineList = new ArrayList<String>();
			dataLineList.add("from: test@out.com\r\n");
			dataLineList.add("subject: the subject\r\n");
			dataLineList.add("\r\n");
			dataLineList.add("content \r\n");
			smtpClient.send("127.0.0.1", 25, "test@test.com", dataLineList);
		}

		for (int i = 0; i < 10; i++) {
			SmtpClient smtpClient = (SmtpClient) this.beanFactory
					.getBean("LocalSmtpClient");

			ArrayList<String> dataLineList = new ArrayList<String>();
			dataLineList.add("from: test@out.com\r\n");
			dataLineList.add("subject: the subject\r\n");
			dataLineList.add("\r\n");
			dataLineList.add("content \r\n");
			smtpClient.send("127.0.0.1", 25, "test1@test.com", dataLineList);
			// smtpClient.send("test@out.com", "test@test.com", dataLineList,
			// "test", "111111");
		}

		Thread.sleep(3000);

		{
			Pop3Client pop3Client = new Pop3Client();
			String[] mailPaths = pop3Client.getMail("127.0.0.1", 110, "test",
					"111111", "/tmp/", true);
			for (String path : mailPaths)
				System.out.println(path);
			assertEquals(mailPaths.length, 10);
		}
		{
			Pop3Client pop3Client = new Pop3Client();
			String[] mailPaths = pop3Client.getMail("127.0.0.1", 110,
					"test1", "111111", "/tmp/", true);
			for (String path : mailPaths)
				System.out.println(path);
			assertEquals(mailPaths.length, 10);
		}
	}

//	public void testSmtpAuth() throws Exception {
//		{
//			SmtpClient smtpClient = (SmtpClient) this.beanFactory
//					.getBean("LocalSmtpClient");
//			smtpClient.setFromEmail("test@test.com");
//			smtpClient.setUsername("test@test1.com");
//			smtpClient.setPassword("111111");
//
//			ArrayList<String> dataLineList = new ArrayList<String>();
//			dataLineList.add("from: test@test1.com\r\n");
//			dataLineList.add("subject: the subject\r\n");
//			dataLineList.add("\r\n");
//			dataLineList.add("content \r\n");
//			smtpClient.send("127.0.0.1", 25,
//					new String[] { "neorayer@gmail.com" }, dataLineList);
//			Thread.sleep(20000);
//			// smtpClient.send("test@out.com", "test@test.com", dataLineList,
//			// "test", "111111");
//		}
//
//	}

}
