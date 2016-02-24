package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.util.List;

import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;
import com.skymiracle.fileBox.MailClass;

/**
 * 邮件存储访问接口
 */
public interface IMailAccessor {

	public List<MailBoxLsItem> mailLsMail(String folderPathInBox)
			throws Exception;

	public String[] mailLsMailUUIDsize(String folderPathInBox) throws Exception;

	public byte[] mailLsMailsBytes(String folderPathInBox) throws Exception;

	public String mailRetr(String folderPathInBox, String mail_UUID)
			throws Exception;

	public void mailStor(String folderPathInBox, String mail_UUID,
			String srcFilePathInFs, boolean isMove) throws Exception;

	public void mailStor(String folderPathInBox, String mail_UUID,
			MailMessage mailMessage, boolean isMove) throws Exception;

	public void mailDelMail(String folderPathInBox, String[] mail_UUIDs)
			throws Exception;

	public List<String> mailLsFldr() throws Exception;

	/**
	 * 取得所有邮件文件夹列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<MailFolderInfo> mailGetFldrInfos() throws Exception;

	public void mailSetRead(String folderPathInBox, String[] mail_UUIDs,
			boolean isRead) throws Exception;

	public void mailSetReply(String folderPathInBox, String[] mail_UUIDs,
			boolean isReply) throws Exception;

	public void mailSetLastModified(String folderPathInBox, String mail_UUID,
			long lastModified) throws Exception;

	public void mailSetStar(String folderPathInBox, String[] mail_UUIDs,
			boolean isStarred) throws Exception;

	public long mailGetStorageSizeUsed() throws Exception;

	public void mailMoveMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception;

	public void mailCopyMail(String srcFolderPathInBox, String[] mail_UUIDs,
			String destFolderPathInBox) throws Exception;

	public void mailEmptyFldr(String folderName) throws Exception;

	public void mailNewFolder(String folderName) throws Exception;

	public void mailDelFolder(String folderName) throws Exception;

	public void mailCreateAlertMail(long mailSize, long spaceAlert)
			throws Exception;

	public void mailAddClass(MailClass mailClass) throws Exception;

	public List<MailClass> mailGetMailClasses() throws Exception;

	public void mailRmMailClass(String mailClassName) throws Exception;
}