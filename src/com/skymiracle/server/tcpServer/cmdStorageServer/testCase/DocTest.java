package com.skymiracle.server.tcpServer.cmdStorageServer.testCase;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.skymiracle.auth.Authable;
import com.skymiracle.fileBox.FileBoxLsItem;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.CmdStorageServer;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IPubDocAccessor;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.StorageAccessorFactory;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IDocAccessor;

public class DocTest extends TestCase {

	private StorageAccessorFactory saf;

	private CmdStorageServer storageServer;

	public DocTest() throws Exception {
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
	
	public void testPubDocLocal() throws Exception {
		if (true)
			return;
		IPubDocAccessor psa = this.saf.getPubStorageDocAccessorLocal("album");
		testPubDoc(psa);
	}
	
	public void testPubDocRemote() throws Exception {
		final CmdStorageServer server = this.storageServer;
		(new Thread() {
			@Override
			public void run() {
				server.start();
			}
		}).start();
		Thread.sleep(1000);
		IPubDocAccessor psa = this.saf.getPubDocStorageAccessor("album","127.0.0.1:6001");
		testPubDoc(psa);
	}

	private void testPubDoc(IPubDocAccessor psa) throws Exception {
		File file = new File("/tmp/abcde");
		StreamPipe.stringToFile("aaaaa",file, "UTF-8");
		psa.storFile(file, "1.txt", true);
		
		File retrFile = psa.retrFile("1.txt");
		String s = StreamPipe.fileToString(retrFile, "UTF-8");
		assertEquals(s, "aaaaa");
		
		psa.deleteFile(new String[]{"1.txt"});
	}
	
	public void testUserDocLocal() throws Exception {
		if (true)
			return;
		IDocAccessor usDocAcc;
		usDocAcc = this.saf.getUserStorageDocAccessor("test", "test.com",
				Authable.LOCATION_NATIVE_LOCAL);
		System.out.println(usDocAcc);
		testUserDoc(usDocAcc);
	}

	public void testUserDocRemote() throws Exception {
		if (true)
			return;
		final CmdStorageServer server = this.storageServer;
		(new Thread() {
			@Override
			public void run() {
				server.start();
			}
		}).start();
		Thread.sleep(1000);
		IDocAccessor usDocAcc;
		usDocAcc = this.saf.getUserStorageDocAccessor("test", "test.com",
				"127.0.0.1:6001");
		System.out.println(usDocAcc);
		testUserDoc(usDocAcc);

		server.stop();
	}

	private void testUserDoc(IDocAccessor usDocAcc) throws Exception {
		// content file use to test
		String content = "ABCDEFG";
		File cFile = new File("/tmp/file1");
		StreamPipe.stringToFile(content, cFile, "ASCII");

		// empty
		usDocAcc.docEmptyFolder("/", true);
		assertEquals(0, usDocAcc.docGetStorageSizeUsed());

		// newFolder
		usDocAcc.docNewFolder("/", "folder1");
		usDocAcc.docNewFolder("/folder1", "folder11");
		usDocAcc.docNewFolder("/folder1", "folder12");
		usDocAcc.docNewFolder("/folder1", "folder13");
		usDocAcc.docNewFolder("/", "folder2");
		usDocAcc.docNewFolder("/folder2", "folder21");
		usDocAcc.docNewFolder("/folder2", "folder22");
		usDocAcc.docNewFolder("/folder2", "folder23");

		// lsFldr
		{
			List<String> dirPathInBoxs = usDocAcc.docLsFldr("/", true);
			for (String dirPathInBox : dirPathInBoxs) {
				System.out.println(dirPathInBox);
			}
			assertEquals(8, dirPathInBoxs.size());
		}

		// storFile
		usDocAcc.docStorFile("/folder1/folder11", "file1", cFile
				.getAbsolutePath(), false);
		assertEquals(7, usDocAcc.docGetStorageSizeUsed());

		// storFile
		usDocAcc.docStorFile("/folder1/folder11", "file2", cFile
				.getAbsolutePath(), false);
		assertEquals(14, usDocAcc.docGetStorageSizeUsed());

		{
			// lsFiles
			List<FileBoxLsItem> fItems = usDocAcc
					.docLsFile("/folder1/folder11");
			assertEquals(2, fItems.size());
		}
		// moveFilesTo
		usDocAcc.docMoveFilesTo(new String[] { "/folder1/folder11/file1",
				"/folder1/folder11/file2" }, "/folder2/folder22");
		{
			// lsFiles
			List<FileBoxLsItem> fItems = usDocAcc
					.docLsFile("/folder1/folder11");
			assertEquals(0, fItems.size());
		}
		{
			// lsFiles
			List<FileBoxLsItem> fItems = usDocAcc
					.docLsFile("/folder2/folder22");
			assertEquals(2, fItems.size());
		}
		usDocAcc.docMoveFilesTo(new String[] { "/folder2/folder22/file1",
				"/folder2/folder22/file2" }, "/folder1/folder11");

		// retrFile
		String tmpPath1 = usDocAcc.docRetrFile("/folder1/folder11", "file1");
		System.out.println(tmpPath1);
		assertEquals(StreamPipe.fileToString(tmpPath1, "UTF-8"), content);
		// retrFile
		String tmpPath2 = usDocAcc.docRetrFile("/folder1/folder11", "file2");
		System.out.println(tmpPath2);
		assertEquals(StreamPipe.fileToString(tmpPath2, "UTF-8"), content);

		// delFiles
		usDocAcc.docDelFiles(new String[] { "/folder1/folder11/file1",
				"/folder1/folder11/file2" });
		assertEquals(0, usDocAcc.docLsFile("/folder1/folder11").size());
		assertEquals(0, usDocAcc.docGetStorageSizeUsed());

		// delFldrs
		usDocAcc.docDelFolder("/folder1/folder11");
		usDocAcc.docDelFolder("/folder1/folder12");
		usDocAcc.docDelFolder("/folder1/folder13");
		usDocAcc.docDelFolder("folder1");
		usDocAcc.docDelFolder("/folder2/folder21");
		usDocAcc.docDelFolder("/folder2/folder22");
		usDocAcc.docDelFolder("/folder2/folder23");
		usDocAcc.docDelFolder("/folder2");
		// lsFldr
		{
			List<String> dirPathInBoxs = usDocAcc.docLsFldr("/", true);
			for (String dirPathInBox : dirPathInBoxs) {
				System.out.println("dir:  " + dirPathInBox);
			}
			assertEquals(0, dirPathInBoxs.size());
		}

	}

}
