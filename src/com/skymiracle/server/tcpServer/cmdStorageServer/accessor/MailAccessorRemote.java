package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.csv.ExcelCSVParser;
import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.fileBox.MailClass;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailAddClassCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailCopyMailCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailCreateAlertMailCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailDelFolderCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailDelMailCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailEmptyFldrCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailGetClassesCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailGetFldrInfosCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailLsFldrCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailLsMailCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailLsMailUUIDsizeCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailMoveMailCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailNewFolderCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailRetrCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailRmClassCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailSetLastModifiedCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailSetReadCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailSetReplyCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailSetStarCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailStorCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserMailStorageSizeUsedCommander;
import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;
import com.skymiracle.util.UUID;
import com.skymiracle.xml.XmlTools;

/**
 * 邮件存储远程访问接口
 */
public class MailAccessorRemote extends IOAccessorRemote implements
		IMailAccessor {

	public MailAccessorRemote(String username, String domain, String host,
			int port, String tmpDirPath, String cacheDirPath, int cacheHashDepth)
			throws IOException {
		super(username, domain, host, port, tmpDirPath, cacheDirPath,
				cacheHashDepth);
	}

	public void mailCopyMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		String s = ocTalkCmd(UserMailCopyMailCommander.class, new String[] {
				srcFolderPathInBox, uuidsToStr(mail_UUIDs),
				"" + destFolderPathInBox });
		checkNoPrefix2Exception(s);
	}

	public void mailDelFolder(String folderName) throws Exception {
		String s = ocTalkCmd(UserMailDelFolderCommander.class,
				new String[] { folderName });
		checkNoPrefix2Exception(s);
	}

	public void mailDelMail(String folderPathInBox, String[] mail_UUIDs)
			throws Exception {
		String s = ocTalkCmd(UserMailDelMailCommander.class, new String[] {
				folderPathInBox, uuidsToStr(mail_UUIDs) });
		checkNoPrefix2Exception(s);
	}

	public void mailEmptyFldr(String folderName) throws Exception {
		String s = ocTalkCmd(UserMailEmptyFldrCommander.class,
				new String[] { folderName });
		checkNoPrefix2Exception(s);
	}

	public long mailGetStorageSizeUsed() throws Exception {
		String l = ocTalkCmd(UserMailStorageSizeUsedCommander.class,
				new String[] {});
		// TODO 这里不安全
		try{
			return Long.parseLong(l);
		}catch(Exception e) {
			Logger.warn("MailAccessorRemote.mailGetStorageSizeUsed(): " + l);
			return 0;
		}
	}

	public List<String> mailLsFldr() throws Exception {
		List<String> folderList = new ArrayList<String>();
		try {
			openSocket();
			String s = talkCmd(UserMailLsFldrCommander.class, new String[] {});
			checkNoPrefix2Exception(s);
			println("ready");
			String content = StreamPipe.inputToString(this.socket
					.getInputStream(), "UTF-8", true);
			String[] lines = content.split("\r\n");
			for (String line : lines) {
				String folderName = line.trim();
				if (folderName.length() == 0)
					continue;
				folderList.add(folderName);
			}
		} finally {
			closeSocket();
		}
		return folderList;
	}

	public List<MailBoxLsItem> mailLsMail(String folderPathInBox)
			throws Exception {
		ByteArrayInputStream bais = null;
		ExcelCSVParser csvParser = null;
		List<MailBoxLsItem> itemList = new LinkedList<MailBoxLsItem>();
		try {
			byte[] bytes = mailLsMailsBytes(folderPathInBox);

			bais = new ByteArrayInputStream(bytes);
			csvParser = new ExcelCSVParser(bais, "UTF-8");
			String[] lineValues = null;
			while ((lineValues = csvParser.getLine()) != null) {
				MailBoxLsItem lsItem = new MailBoxLsItem(lineValues);
				itemList.add(lsItem);
			}
		} finally {
			if (csvParser != null)
				csvParser.close();
			if (bais != null)
				bais.close();
		}
		return itemList;
	}

	public String[] mailLsMailUUIDsize(String folderPathInBox) throws Exception {
		try {
			openSocket();
			String s = talkCmd(UserMailLsMailUUIDsizeCommander.class,
					new String[] { folderPathInBox });
			checkNoPrefix2Exception(s);
			println("ready");
			String content = StreamPipe.inputToString(this.socket
					.getInputStream(), "UTF-8", true);
			return content.split("\r\n");
		} finally {
			closeSocket();
		}
	}

	public byte[] mailLsMailsBytes(String folderPathInBox) throws Exception {
		try {
			openSocket();
			String s = talkCmd(UserMailLsMailCommander.class,
					new String[] { folderPathInBox });
			checkNoPrefix2Exception(s);
			println("ready");
			byte[] bytes = StreamPipe.inputToBytes(
					this.socket.getInputStream(), true);
			return bytes;
		} finally {
			closeSocket();
		}
	}

	public void mailMoveMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		String s = ocTalkCmd(UserMailMoveMailCommander.class, new String[] {
				srcFolderPathInBox, uuidsToStr(mail_UUIDs),
				"" + destFolderPathInBox });
		checkNoPrefix2Exception(s);
	}

	public void mailNewFolder(String folderName) throws Exception {
		String s = ocTalkCmd(UserMailNewFolderCommander.class,
				new String[] { folderName });
		checkNoPrefix2Exception(s);
	}

	public String mailRetr(String folderPathInBox, String mail_UUID)
			throws Exception {
		File localFile = new File(getUserCacheDirPath(), mail_UUID);
		if (localFile.exists())
			return localFile.getAbsolutePath();
		try {
			openSocket();
			String s = talkCmd(UserMailRetrCommander.class, new String[] {
					folderPathInBox, mail_UUID });
			checkNoPrefix2Exception(s);
			println("ready");
			localFile.getParentFile().mkdirs();
			StreamPipe.inputToFile(this.socket.getInputStream(), localFile,
					true);
			return localFile.getAbsolutePath();
		} finally {
			closeSocket();
		}
	}

	public void mailSetLastModified(String folderPathInBox, String mail_UUID,
			long lastModified) throws Exception {
		String s = ocTalkCmd(UserMailSetLastModifiedCommander.class,
				new String[] { folderPathInBox, mail_UUID, "" + lastModified });
		checkNoPrefix2Exception(s);
	}

	private String uuidsToStr(String[] uuids) {
		StringBuffer sb = new StringBuffer();
		for (String uuid : uuids)
			sb.append(uuid).append("|");
		return sb.toString();
	}

	public void mailSetRead(String folderPathInBox, String[] mail_UUIDs,
			boolean isRead) throws Exception {
		String s = ocTalkCmd(UserMailSetReadCommander.class, new String[] {
				folderPathInBox, uuidsToStr(mail_UUIDs), "" + isRead });
		checkNoPrefix2Exception(s);
	}

	public void mailSetReply(String folderPathInBox, String[] mail_UUIDs,
			boolean isReply) throws Exception {
		String s = ocTalkCmd(UserMailSetReplyCommander.class, new String[] {
				folderPathInBox, uuidsToStr(mail_UUIDs), "" + isReply });
		checkNoPrefix2Exception(s);
	}

	public void mailSetStar(String folderPathInBox, String[] mail_UUIDs,
			boolean isStarred) throws Exception {
		String s = ocTalkCmd(UserMailSetStarCommander.class, new String[] {
				folderPathInBox, uuidsToStr(mail_UUIDs), "" + isStarred });
		checkNoPrefix2Exception(s);
	}

	public void mailStor(String folderPathInBox, String mail_UUID,
			String srcFilePathInFs, boolean isMove) throws Exception {
		try {
			openSocket();
			String s = talkCmd(UserMailStorCommander.class, new String[] {
					folderPathInBox, mail_UUID });
			checkNoPrefix2Exception(s);
			File srcFile = new File(srcFilePathInFs);
			StreamPipe.fileToOutput(srcFile, this.socket.getOutputStream(),
					false);
			if (isMove)
				srcFile.delete();
		} finally {
			closeSocket();
		}
	}

	public void mailStor(String folderPathInBox, String mail_UUID,
			MailMessage mailMessage, boolean isMove) throws Exception {
		String tmpFilePath = this.tmpDirPath + "/" + new UUID().toShortString();
		TextFile.save(tmpFilePath, mailMessage.getDataLineList(), "ISO-8859-1");
		mailStor(folderPathInBox, mail_UUID, tmpFilePath, isMove);
	}

	public void mailCreateAlertMail(long mailSize, long spaceAlert)
			throws Exception {
		String s = ocTalkCmd(UserMailCreateAlertMailCommander.class,
				new String[] { "" + mailSize, "" + spaceAlert });
		checkNoPrefix2Exception(s);
	}

	public List<MailFolderInfo> mailGetFldrInfos() throws Exception {
		List<MailFolderInfo> infos = new LinkedList<MailFolderInfo>();
		try {
			// 连接存储服务器
			openSocket();
			// 发送“USERMAILGETFLDRINFOS”命令, 接收存储服务器返回信息
			String s = talkCmd(UserMailGetFldrInfosCommander.class,
					new String[] {});
			checkNoPrefix2Exception(s);
			println("ready");
			String xml = StreamPipe.inputToString(this.socket.getInputStream(),
					"UTF-8", true);
			Object[] objs = XmlTools.getObjects(MailFolderInfo.class
					.getSimpleName(), xml);
			for (Object obj : objs) {
				infos.add((MailFolderInfo) obj);
			}
		} finally {
			closeSocket();
		}
		return infos;
	}

	public void mailAddClass(MailClass mc) throws Exception {
		String s = ocTalkCmd(UserMailAddClassCommander.class, new String[] {
				mc.getMailClassName(), mc.getMailClassFolderName(),
				mc.getTarget(), mc.getOp(), mc.getKeyWord() });
		checkNoPrefix2Exception(s);
	}

	public List<MailClass> mailGetMailClasses() throws Exception {
		ExcelCSVParser csvParser = null;
		ByteArrayInputStream bais = null;
		List<MailClass> mClassList = new ArrayList<MailClass>();
		try {
			openSocket();
			String s = talkCmd(UserMailGetClassesCommander.class,
					new String[] {});
			checkNoPrefix2Exception(s);
			println("ready");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamPipe.inputToOutput(this.socket.getInputStream(), baos, false);
			closeSocket();
			byte[] bytes = baos.toByteArray();
			bais = new ByteArrayInputStream(bytes);
			csvParser = new ExcelCSVParser(bais, "ISO-8859-1");
			String[] lineValues = null;
			while ((lineValues = csvParser.getLine()) != null) {
				MailClass mailClass = new MailClass(lineValues[0],
						lineValues[1], lineValues[2], lineValues[3],
						lineValues[4]);
				mClassList.add(mailClass);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (csvParser != null)
				csvParser.close();
			if (bais != null)
				bais.close();
			closeSocket();
		}
		return mClassList;
	}

	public void mailRmMailClass(String mailClassName) throws Exception {
		String s = ocTalkCmd(UserMailRmClassCommander.class,
				new String[] { mailClassName });
		checkNoPrefix2Exception(s);
	}
}
