package com.skymiracle.server.tcpServer.mailServer.wp8;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.fileBox.MailClass;
import com.skymiracle.fileBox.MailFileBox;
import com.skymiracle.io.Dir;
import com.skymiracle.io.TextFile;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.MailFolderInfo;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;
import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;
import com.skymiracle.util.UUID;

public class Wp8MailAccessor implements IMailAccessor {
	private String rootPathInFs = null;

	public Wp8MailAccessor(String username, String domain,
			String location) {
		this.rootPathInFs = location + "/Maildir/cur";
	}

	private String getPathInFs(String pathInBox) {
		return new File(this.rootPathInFs, pathInBox).getAbsolutePath();
	}

	private String findPathInFs(String folderPathInBox, String uuid) {
		String pathInBox = findPathInBox(folderPathInBox, uuid);
		if (pathInBox == null)
			return null;
		return this.getPathInFs(pathInBox);
	}

	private void mvCommonFile(String srcPathInBox, String targetPathInBox)
			throws Exception {
		String srcPathInFs = getPathInFs(srcPathInBox);
		String targetPathInFs = getPathInFs(targetPathInBox);
		boolean isSucc = new File(srcPathInFs)
				.renameTo(new File(targetPathInFs));
		if (!isSucc)
			throw new Exception("Can not rename file " + srcPathInFs + " to "
					+ targetPathInFs);
	}

	private boolean exists(String pathInBox) {
		String pathInFs = getPathInFs(pathInBox);
		return new File(pathInFs).exists();
	}

	
	private String findPathInBox(String folderPathInBox, String uuid) {
		String s;

		// 为了POP3忽略了folderPathInBox
		String pathInBox = uuid;
		s = this.getPathInFs(pathInBox);
		if (new File(s).exists())
			return pathInBox;

		String pathInBox2 = pathInBox + "!2,S";
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// reply
		pathInBox2 = pathInBox + "!3,S";
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// old mail
		pathInBox2 = pathInBox + ",S";
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// ---starred,modified by disller.
		String pathInBox3 = folderPathInBox + "/starred" + uuid;
		s = this.getPathInFs(pathInBox3);
		if (new File(s).exists())
			return pathInBox3;

		String pathInBox4 = pathInBox3 + "!2,S";
		s = this.getPathInFs(pathInBox4);
		if (new File(s).exists())
			return pathInBox4;

		// reply
		pathInBox4 = pathInBox3 + "!3,S";
		s = this.getPathInFs(pathInBox4);
		if (new File(s).exists())
			return pathInBox4;

		return null;
	}

	private void rmCommonFileInFs(String pathInFs) throws Exception {
		long fileSize = new File(pathInFs).length();
		boolean isSucc = new File(pathInFs).delete();
		if (!isSucc)
			throw new Exception("Can not remove file:" + pathInFs);
		//modSize(-fileSize);
	}

	public void mailDelMail(String folderPathInBox, String[] mail_UUIDs)
			throws Exception {
		for (String uuid : mail_UUIDs) {
			if (uuid.trim().length() == 0)
				continue;
			String s = findPathInFs(folderPathInBox, uuid);
			if (s == null)
				continue;
			rmCommonFileInFs(s);
		}
	}

	public String mailRetr(String folderPathInBox, String mail_UUID)
			throws Exception {
		return findPathInFs(folderPathInBox, mail_UUID);
	}

	public void mailSetRead(String folderPathInBox, String[] mail_UUIDs,
			boolean isRead) throws Exception {
		for (String uuid : mail_UUIDs) {
			if (uuid.trim().length() == 0)
				continue;
			String oldPathInBox = findPathInBox(folderPathInBox, uuid);
			if (isRead && !oldPathInBox.endsWith(",S")) {
				String newPathInBox = oldPathInBox + "!2,S";
				if (!exists(newPathInBox)) {
					long time = (new File(oldPathInBox)).lastModified();
					this.mvCommonFile(oldPathInBox, newPathInBox);
					(new File(newPathInBox)).setLastModified(time);
				}
			} else if (!isRead && oldPathInBox.endsWith(",S")) {
				String newPathInBox = oldPathInBox;
				newPathInBox = oldPathInBox.substring(0,
						oldPathInBox.length() - 4);
				if (!exists(newPathInBox)) {
					long time = (new File(oldPathInBox)).lastModified();
					this.mvCommonFile(oldPathInBox, newPathInBox);
					(new File(newPathInBox)).setLastModified(time);
				}
			}
		}
	}
	

	public List<MailBoxLsItem> mailLsMail(String folderPathInBox)
		throws Exception {
		List<MailBoxLsItem> itemList = new LinkedList<MailBoxLsItem>();
		Dir dir = new Dir(this.rootPathInFs);
		File[] files = dir.listFiles();
		
		for (File fileInfo : files) {
			String uuid = MailFileBox.fileNameToUUID(fileInfo.getName());
			MailBoxLsItem lsItem = new MailBoxLsItem();
			lsItem.setUuid(uuid);
			lsItem.setSize(fileInfo.length());
			itemList.add(lsItem);
		}
		return itemList;
	}

	// ////////////// Bellows are not used by pop3 //////////////////////////
	public byte[] mailLsMailsBytes(String folderPathInBox) throws Exception {
		throw new Exception("Not Implemented");
	}


	public String[] mailLsMailUUIDsize(String folderPathInBox) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailAddClass(MailClass mailClass) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailCopyMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailCreateAlertMail(long mailSize, long spaceAlert)
			throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailDelFolder(String folderName) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailEmptyFldr(String folderName) throws Exception {
		throw new Exception("Not Implemented");
	}

	public List<MailFolderInfo> mailGetFldrInfos() throws Exception {
		throw new Exception("Not Implemented");
	}

	public List<MailClass> mailGetMailClasses() throws Exception {
		throw new Exception("Not Implemented");
	}

	public long mailGetStorageSizeUsed() throws Exception {
			return -1;
	}

	public List<String> mailLsFldr() throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailMoveMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailNewFolder(String folderName) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailRmMailClass(String mailClassName) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailSetLastModified(String folderPathInBox, String mail_UUID,
			long lastModified) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailSetReply(String folderPathInBox, String[] mail_UUIDs,
			boolean isReply) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailSetStar(String folderPathInBox, String[] mail_UUIDs,
			boolean isStarred) throws Exception {
		throw new Exception("Not Implemented");
	}

	public void mailStor(String folderPathInBox, String mail_UUID,
			String srcFilePathInFs, boolean isMove) throws Exception {
		new MailFileBox(this.rootPathInFs).newMail(folderPathInBox, mail_UUID, srcFilePathInFs,
				isMove);
	}

	public void mailStor(String folderPathInBox, String mail_UUID,
			MailMessage mailMessage, boolean isMove) throws Exception {
		String tmpFilePath = "/tmp" + "/" + new UUID().toShortString();
		TextFile.save(tmpFilePath, mailMessage.getDataLineList());
		mailStor("/", mail_UUID, tmpFilePath, isMove);
	}

}
