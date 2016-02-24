package com.skymiracle.server.tcpServer.cmdStorageServer.testCase;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.auth.Authable;
import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.CmdStorageServer;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.MailFolderInfo;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.StorageAccessorFactory;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;
import com.skymiracle.util.UUID;

public class MailTest extends TestCase {

	private StorageAccessorFactory saf;

	private CmdStorageServer storageServer;

	public MailTest() throws Exception {
		Logger.setLevel(Logger.LEVEL_DETAIL);

		String path = this.getClass().getPackage().getName();
		path = path.replace('.', '/') + "/spring.xml";
		Resource resource = new ClassPathResource(path);
		BeanFactory beanFactory = new XmlBeanFactory(resource);
		this.saf = (StorageAccessorFactory) beanFactory
				.getBean("StorageAccessorFactory");
		this.storageServer = (CmdStorageServer) beanFactory
				.getBean("StorageServer");

	}

	public void testUserMailLocal() throws Exception {
		IMailAccessor usMailAcc;
		usMailAcc = this.saf.getUserStorageMailAccessor("test", "test.com",
				Authable.LOCATION_NATIVE_LOCAL);
		System.out.println(usMailAcc);
		testUserMail(usMailAcc);
	}

	public void testUserMailRemote() throws Exception {
		final CmdStorageServer server = this.storageServer;
		(new Thread() {
			@Override
			public void run() {
				server.start();
			}
		}).start();
		Thread.sleep(1000);
		IMailAccessor usMailAcc;
		usMailAcc = this.saf.getUserStorageMailAccessor("test", "test.com",
				"127.0.0.1:6001");
		System.out.println(usMailAcc);
		testUserMail(usMailAcc);

		server.stop();
	}

	private void testUserMail(IMailAccessor usMailAcc) throws Exception {

		{
			List<String> folders = usMailAcc.mailLsFldr();
			for (String folder : folders) {
				usMailAcc.mailEmptyFldr(folder);
				if ("inbox".equals(folder))
					continue;
				if ("trash".equals(folder))
					continue;
				if ("draft".equals(folder))
					continue;
				if ("sent".equals(folder))
					continue;
				usMailAcc.mailDelFolder(folder);
			}
		}
		assertEquals(0, usMailAcc.mailGetStorageSizeUsed());

		usMailAcc.mailNewFolder("myFolder1");
		usMailAcc.mailNewFolder("myFolder2");
		usMailAcc.mailNewFolder("myFolder3");
		usMailAcc.mailNewFolder("myFolder4");

		List<String> folders = usMailAcc.mailLsFldr();
		assertEquals(8, folders.size());

		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(0, items.size());
		}

		String mailUUID1 = new UUID().toShortString();
		File mailFile1 = new File("/tmp/eml1.eml");
		{
			InputStream is = ClassLoader
					.getSystemResourceAsStream("com/skymiracle/server/tcpServer/cmdStorageServer/testCase/resource/eml1.eml");
			StreamPipe.inputToFile(is, mailFile1, true);
			usMailAcc.mailStor("inbox", mailUUID1, mailFile1.getAbsolutePath(),
					true);
		}
		String mailUUID2 = new UUID().toShortString();
		File mailFile2 = new File("/tmp/eml1.eml");
		{
			InputStream is = ClassLoader
					.getSystemResourceAsStream("com/skymiracle/server/tcpServer/cmdStorageServer/testCase/resource/eml2.eml");
			StreamPipe.inputToFile(is, mailFile2, true);
			usMailAcc.mailStor("inbox", mailUUID2, mailFile2.getAbsolutePath(),
					true);
		}
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(2, items.size());
			assertEquals(false, items.get(0).getReaded());
			assertEquals(false, items.get(1).getReaded());
		}
		usMailAcc.mailSetRead("inbox", new String[] { mailUUID1, mailUUID2 },
				true);
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(2, items.size());
			assertEquals(true, items.get(0).getReaded());
			assertEquals(true, items.get(1).getReaded());
		}

		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(2, items.size());
			assertEquals(false, items.get(0).getReply());
			assertEquals(false, items.get(1).getReply());
		}
		usMailAcc.mailSetReply("inbox", new String[] { mailUUID1, mailUUID2 },
				true);
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(2, items.size());
			assertEquals(true, items.get(0).getReply());
			assertEquals(true, items.get(1).getReply());
		}

		{
			String path = usMailAcc.mailRetr("inbox", mailUUID1);
			assertEquals(new File(path).length(), 3083);
		}

		usMailAcc.mailCopyMail("inbox", new String[] { mailUUID1 }, "trash");
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("trash");
			assertEquals(1, items.size());
			String path = usMailAcc.mailRetr("trash", mailUUID1);
			assertEquals(new File(path).length(), 3083);
		}

		usMailAcc.mailDelMail("inbox", new String[] { mailUUID1 });
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(1, items.size());
		}

		byte[] bytes = usMailAcc.mailLsMailsBytes("inbox");
		System.out.write(bytes);

		String[] ss = usMailAcc.mailLsMailUUIDsize("inbox");
		assertEquals(1, ss.length);
		assertEquals(mailUUID2 + " " + 51005, ss[0]);

		usMailAcc.mailMoveMail("inbox", new String[] { mailUUID2 }, "sent");
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(0, items.size());
		}
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("sent");
			assertEquals(1, items.size());
		}

		usMailAcc.mailCreateAlertMail(1000, 1000);
		{
			List<MailBoxLsItem> items = usMailAcc.mailLsMail("inbox");
			assertEquals(1, items.size());
		}

		{
			List<MailFolderInfo> infos = usMailAcc.mailGetFldrInfos();
			assertEquals(8, infos.size());
		}
	}

}
