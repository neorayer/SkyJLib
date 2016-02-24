package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.fileBox.MailBoxLsReaderCacheCsvImpl;
import com.skymiracle.fileBox.MailFileBox;
import com.skymiracle.fileBox.MailFileBox.MailFileBoxFolderInfo;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;
import com.skymiracle.util.UUID;
import com.skymiracle.fileBox.MailClass;

/**
 * 邮件存储本地访问接口
 */
public class MailAccessorLocal extends IOAccessorLocal implements IMailAccessor {

	// 根目录/mailbox
	private MailFileBox mailFileBox;

	public MailAccessorLocal(String username, String domain, String rootPath,
			HomeDirConfiger homeDirConfiger, String tmpDirPath)
			throws Exception {
		super(username, domain, "mailbox", rootPath, homeDirConfiger,
				tmpDirPath);
		this.mailFileBox = new MailFileBox(this.homePath);
		initCreate();
	}

	private void initCreate() throws Exception {
		// 收件箱文件夹
		this.mailFileBox.mkDirIfNotExist("/inbox");
		// 草稿箱文件夹
		this.mailFileBox.mkDirIfNotExist("/draft");
		// 废件箱文件夹
		this.mailFileBox.mkDirIfNotExist("/trash");
		// 已发送邮件文件夹
		this.mailFileBox.mkDirIfNotExist("/sent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailLsMail
	 */
	public List<MailBoxLsItem> mailLsMail(String folderPathInBox)
			throws Exception {
		return this.mailFileBox.lsMail(folderPathInBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailLsMailUUIDsize(java.lang.String)
	 */
	public String[] mailLsMailUUIDsize(String folderPathInBox) {
		return this.mailFileBox.lsMailUUIDsize(folderPathInBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailLsMailsBytes(java.lang.String)
	 */
	public byte[] mailLsMailsBytes(String folderPathInBox) throws Exception {
		MailBoxLsReaderCacheCsvImpl lsReader = new MailBoxLsReaderCacheCsvImpl(
				this.mailFileBox, folderPathInBox);
		return lsReader.lsMailCsvBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailRetr(java.lang.String,
	 *      java.lang.String)
	 */
	public String mailRetr(String folderPathInBox, String mail_UUID)
			throws Exception {
		return mailfindPathInFs(folderPathInBox, mail_UUID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailfindPathInFs(java.lang.String,
	 *      java.lang.String)
	 */
	public String mailfindPathInFs(String folderPathInBox, String mail_UUID)
			throws Exception {
		String s = this.mailFileBox.findPathInFs(folderPathInBox, mail_UUID);
		if (s == null)
			throw new Exception("Can not find folder=" + folderPathInBox
					+ " mail_UUID=" + mail_UUID);
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailStor(java.lang.String,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	public void mailStor(String folderPathInBox, String mail_UUID,
			String srcFilePathInFs, boolean isMove) throws Exception {
		this.mailFileBox.newMail(folderPathInBox, mail_UUID, srcFilePathInFs,
				isMove);
	}

	public void mailStor(String folderPathInBox, String mail_UUID,
			MailMessage mailMessage, boolean isMove) throws Exception {
		String tmpFilePath = this.tmpDirPath + "/" + new UUID().toShortString();
		TextFile.save(tmpFilePath, mailMessage.getDataLineList());
		mailStor(folderPathInBox, mail_UUID, tmpFilePath, isMove);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailStor(java.lang.String,
	 *      java.lang.String, java.io.InputStream, boolean)
	 */
	public void mailStor(String folderPathInBox, String mail_UUID,
			InputStream inputStream, boolean isClose) throws Exception {
		String tmpFilePath = this.tmpDirPath + "/" + new UUID().toShortString();
		StreamPipe.inputToFile(inputStream, tmpFilePath, isClose);
		mailStor(folderPathInBox, mail_UUID, tmpFilePath, true);
		Logger.debug("UserStorageMailAccessorLocal saved to native mailbox("
				+ this.username + '@' + this.domain + ") " + mail_UUID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailDeleMail(java.lang.String,
	 *      java.lang.String)
	 */
	public void mailDelMail(String folderPathInBox, String[] mail_UUIDs)
			throws Exception {
		this.mailFileBox.deles(folderPathInBox, mail_UUIDs);
	}

	/**
	 * 邮件主目录下的所有文件夹名
	 */
	public List<String> mailLsFldr() throws Exception {
		return this.mailFileBox.lsDirNames("/");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailSetRead(java.lang.String,
	 *      java.lang.String, boolean)
	 */
	public void mailSetRead(String folderPathInBox, String[] mail_UUIDs,
			boolean isRead) throws Exception {
		this.mailFileBox.setRead(folderPathInBox, mail_UUIDs, isRead);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailSetReply(java.lang.String,
	 *      java.lang.String, boolean)
	 */
	public void mailSetReply(String folderPathInBox, String[] mail_UUIDs,
			boolean isReply) throws Exception {
		this.mailFileBox.setReply(folderPathInBox, mail_UUIDs, isReply);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailSetLastModified(java.lang.String,
	 *      java.lang.String, long)
	 */
	public void mailSetLastModified(String folderPathInBox, String mail_UUID,
			long lastModified) throws Exception {
		this.mailFileBox.setLastModified(folderPathInBox, mail_UUID,
				lastModified);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailSetStar(java.lang.String,
	 *      java.lang.String, boolean)
	 */
	public void mailSetStar(String folderPathInBox, String[] mail_UUIDs,
			boolean isStarred) throws Exception {
		this.mailFileBox.setStar(folderPathInBox, mail_UUIDs, isStarred);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailGetStorageSizeUsed()
	 */
	public long mailGetStorageSizeUsed() throws IOException {
		return this.mailFileBox.df();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailMoveMail(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void mailMoveMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		this.mailFileBox.moveMail(srcFolderPathInBox, mail_UUIDs,
				destFolderPathInBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailCopyMail(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void mailCopyMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception {
		this.mailFileBox.copyMail(srcFolderPathInBox, mail_UUIDs,
				destFolderPathInBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.server.tcpServer.cmdStorageServer.accessor.UserStorageMailAccessor1#mailEmptFldr(java.lang.String)
	 */
	public void mailEmptyFldr(String folderName) throws Exception {
		this.mailFileBox.emptyDir(folderName, false);
	}

	public void mailNewFolder(String folderName) throws Exception {
		this.mailFileBox.mkDir("/" + folderName);
	}

	public void mailDelFolder(String folderName) throws Exception {
		if (folderName.trim().length() == 0)
			return;
		this.mailFileBox.rmDir("/" + folderName);

	}

	public void mailCreateAlertMail(long mailSize, long spaceAlert)
			throws Exception {
		this.mailFileBox.creatAlertMail(mailSize, spaceAlert);
	}

	/**
	 * 取得所有邮件文件夹列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<MailFolderInfo> mailGetFldrInfos() throws Exception {
		List<MailFolderInfo> mailFolderInfos = new LinkedList<MailFolderInfo>();
		List<String> folderNames = this.mailLsFldr();
		for (String folderName : folderNames) {
			MailFileBoxFolderInfo info = this.mailFileBox.getInfo("/"
					+ folderName);
			MailFolderInfo mailFolderInfo = new MailFolderInfo();
			mailFolderInfo.setIdxName(folderName);
			mailFolderInfo.setCount(info.getCount());
			mailFolderInfo.setNewCount(info.getNewCount());

			mailFolderInfos.add(mailFolderInfo);
		}

		return mailFolderInfos;
	}

	public void mailAddClass(MailClass mc) throws IOException {
		this.mailFileBox.addClass(mc.getMailClassName(), mc
				.getMailClassFolderName(), mc.getTarget(), mc.getOp(), mc
				.getKeyWord());
	}

	public List<MailClass> mailGetMailClasses() throws IOException {
		return this.mailFileBox.getClasses();
	}

	public byte[] mailGetMailClassesBytes() throws IOException {
		return this.mailFileBox.getClassesBytes();
	}

	public void mailRmMailClass(String mailClassName) throws IOException {
		this.mailFileBox.rmMailClass(mailClassName);
	}

}
