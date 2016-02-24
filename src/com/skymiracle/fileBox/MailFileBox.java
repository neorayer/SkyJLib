package com.skymiracle.fileBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.csv.Csv;
import com.skymiracle.io.TextFile;
import com.skymiracle.server.tcpServer.mailServer.AutoMailMaker;
import com.skymiracle.util.UUID;

/**
 * name example: 2k4jh34j4_subject.eml
 * 邮件主目录
 * @author neora
 * 
 */
public class MailFileBox extends FileBoxImpl {

	// 根目录下mailclass文件物理路径
	private String mailClassPathInFs;

	// 根目录下replymail文件物理路径
	private String replyMailPathInFs;

	private String charset = "UTF-8";
	
	/**
	 * 已读
	 */
	public static final String MAIL_FILE_SUFFIX_2 = "!2,S";
	
	/**
	 * 已回复
	 */
	public static final String MAIL_FILE_SUFFIX_3 = "!3,S";
	
	/**
	 * 已写入cache.csv
	 */
	public static final String MAIL_FILE_SUFFIX_S = ",S";
	
	/**
	 * 已加星标
	 */
	public static final String MAIL_FILE_PREFIX_STAR = "starred";
	
	
	
	public MailFileBox(String rootPathInFs) {
		super(rootPathInFs);
		this.mailClassPathInFs = this.getRootPathInFs() + "/mailclass";
		this.replyMailPathInFs = this.getRootPathInFs() + "/replymail";
		// TODO Auto-generated constructor stub
	}

	public List<MailBoxLsItem> lsMail(String pathInBox) throws Exception {
		MailBoxLsReaderCacheCsvImpl lsReader = new MailBoxLsReaderCacheCsvImpl(
				this, pathInBox);
		List<MailBoxLsItem> items = lsReader.lsMail();
		return items;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public void emptyDir(String pathInBox, boolean isIncSub) throws Exception {
		emptyDir(pathInBox, isIncSub, new String[] { ".size", "cache.csv" });
		// new File(this.getPathInFs(pathInBox) + "/cache.csv").delete();
	}

	/**
	 * 文件名转成邮件UUID
	 * @param fileName
	 * @return
	 */
	public static String fileNameToUUID(String fileName) {
		String uuid = fileName;
		// !2,S !3,S
		if (fileName.endsWith(MAIL_FILE_SUFFIX_2) || fileName.endsWith(MAIL_FILE_SUFFIX_3))
			uuid = fileName.substring(0, fileName.length() - 4); // !2,S||!3,S
		// length 4
		else if (fileName.endsWith(MAIL_FILE_SUFFIX_S))
			uuid = fileName.substring(0, fileName.length() - 2);
		// -->starred
		if (uuid.startsWith(MAIL_FILE_PREFIX_STAR))
			uuid = uuid.substring(7);

		return uuid;
	}

	public String findPathInFs(String folderPathInBox, String uuid) {
		String pathInBox = findPathInBox(folderPathInBox, uuid);
		if (pathInBox == null)
			return null;
		return this.getPathInFs(pathInBox);
	}

	public String findPathInBox(String folderPathInBox, String uuid) {
		String s;

		String pathInBox = folderPathInBox + "/" + uuid;
		s = this.getPathInFs(pathInBox);
		if (new File(s).exists())
			return pathInBox;

		String pathInBox2 = pathInBox + MAIL_FILE_SUFFIX_2;
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// reply
		pathInBox2 = pathInBox + MAIL_FILE_SUFFIX_3;
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// old mail
		pathInBox2 = pathInBox + MAIL_FILE_SUFFIX_S;
		s = this.getPathInFs(pathInBox2);
		if (new File(s).exists())
			return pathInBox2;

		// ---starred,modified by disller.
		String pathInBox3 = folderPathInBox + "/starred" + uuid;
		s = this.getPathInFs(pathInBox3);
		if (new File(s).exists())
			return pathInBox3;

		String pathInBox4 = pathInBox3 + MAIL_FILE_SUFFIX_2;
		s = this.getPathInFs(pathInBox4);
		if (new File(s).exists())
			return pathInBox4;

		// reply
		pathInBox4 = pathInBox3 + MAIL_FILE_SUFFIX_3;
		s = this.getPathInFs(pathInBox4);
		if (new File(s).exists())
			return pathInBox4;

		return null;
	}

	public void deles(String folderPathInBox, String[] uuids) throws Exception {
		for (int i = 0; i < uuids.length; i++) {
			if (uuids[i].trim().length() == 0)
				continue;
			String s = findPathInFs(folderPathInBox, uuids[i]);
			if (s == null)
				throw new Exception(
						"MailBox.deles(). Can not find folderPathInBox="
								+ folderPathInBox + " mail_UUID=" + uuids[i]);
			rmCommonFileInFs(s);
			setUpdateFlag(folderPathInBox, true);
		}
	}

	public void moveMail(String srcFolderPathInBox, String[] uuids,
			String destFolderPathInBox) throws Exception {
		int mvnum = 0;
		for (int i = 0; i < uuids.length; i++) {
			if (uuids[i].trim().length() == 0)
				continue;
			String srcPathInFs = findPathInFs(srcFolderPathInBox, uuids[i]);
			String fileName = new File(srcPathInFs).getName();
			String srcPathInBox = srcFolderPathInBox + "/" + fileName;
			String destPathInBox = destFolderPathInBox + "/" + fileName;
			mvCommonFile(srcPathInBox, destPathInBox);
			mvnum++;
		}
		if (mvnum > 0) {
			setUpdateFlag(srcFolderPathInBox, true);
			setUpdateFlag(destFolderPathInBox, true);
		}
	}

	public void copyMail(String srcFolderPathInBox, String[] uuids,
			String destFolderPathInBox) throws Exception {
		int cpnum = 0;
		for (int i = 0; i < uuids.length; i++) {
			if (uuids[i].trim().length() == 0)
				continue;
			String srcPathInFs = findPathInFs(srcFolderPathInBox, uuids[i]);
			String fileName = new File(srcPathInFs).getName();
			String srcPathInBox = srcFolderPathInBox + "/" + fileName;
			String destPathInBox = destFolderPathInBox + "/" + fileName;
			cpCommonFile(srcPathInBox, destPathInBox);
			cpnum++;
		}
		if (cpnum > 0)
			setUpdateFlag(destFolderPathInBox, true);
	}

	public void setReply(String folderPathInBox, String[] uuids, boolean isReply)
			throws Exception {
		for (int i = 0; i < uuids.length; i++) {
			String uuid = uuids[i];
			if (uuid.trim().length() == 0)
				continue;
			String oldPathInBox = findPathInBox(folderPathInBox, uuid);

			if (isReply && oldPathInBox.endsWith(MAIL_FILE_SUFFIX_2)) {
				String newPathInBox = oldPathInBox.substring(0, oldPathInBox
						.length() - 4)
						+ MAIL_FILE_SUFFIX_3;
				if (!exists(newPathInBox)) {
					long time = (new File(oldPathInBox)).lastModified();
					this.mvCommonFile(oldPathInBox, newPathInBox);
					(new File(newPathInBox)).setLastModified(time);
					setUpdateFlag(folderPathInBox, true);
				}
			}
		}
	}

	public void setRead(String folderPathInBox, String[] uuids, boolean isRead)
			throws Exception {
		for (int i = 0; i < uuids.length; i++) {
			String uuid = uuids[i];
			if (uuid.trim().length() == 0)
				continue;
			String oldPathInBox = findPathInBox(folderPathInBox, uuid);
			if (isRead && !oldPathInBox.endsWith(MAIL_FILE_SUFFIX_S)) {
				String newPathInBox = oldPathInBox + MAIL_FILE_SUFFIX_2;
				if (!exists(newPathInBox)) {
					long time = (new File(oldPathInBox)).lastModified();
					this.mvCommonFile(oldPathInBox, newPathInBox);
					(new File(newPathInBox)).setLastModified(time);
					setUpdateFlag(folderPathInBox, true);
				}
			} else if (!isRead && oldPathInBox.endsWith(MAIL_FILE_SUFFIX_S)) {
				String newPathInBox = oldPathInBox;
				newPathInBox = oldPathInBox.substring(0,
						oldPathInBox.length() - 4);
				if (!exists(newPathInBox)) {
					long time = (new File(oldPathInBox)).lastModified();
					this.mvCommonFile(oldPathInBox, newPathInBox);
					(new File(newPathInBox)).setLastModified(time);
					setUpdateFlag(folderPathInBox, true);
				}
			}
		}

	}

	public void setLastModified(String folderPathInBox, String uuid,
			long lastModified) {
		String pathInFs = this.findPathInFs(folderPathInBox, uuid);
		new File(pathInFs).setLastModified(lastModified);
	}

	public boolean isRead(String folderPathInBox, String uuid) {
		// -- > starred

		String newPathInBox = findPathInBox(folderPathInBox, uuid);
		return newPathInBox.endsWith(MAIL_FILE_SUFFIX_2);

		// String newPathInBox = folderPathInBox + "/" + uuid + "!2,S";
		// return exists(newPathInBox);
	}

	public void newMail(String folderPathInBox, String uuid,
			String srcPathInFs, boolean isMove) throws IOException {
		String pathInBox = folderPathInBox + "/" + uuid;
		newCommonFile(pathInBox, srcPathInFs, isMove);
		try {
			setUpdateFlag(folderPathInBox, true);
		} catch (Exception e) {
		}
	}

	public String[] lsMailUUIDsize(String folderPathInBox) {
		ArrayList<String> uuidList = new ArrayList<String> ();
		String pathInFs = this.getPathInFs(folderPathInBox);
		File dir = new File(pathInFs);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			if (fileName.equals("cache.csv"))
				continue;
			if (fileName.equals("update"))
				continue;
			String s = fileNameToUUID(fileName) + " " + files[i].length();
			uuidList.add(s);
		}
		return uuidList.toArray(new String[0]);
	}

	public void setStar(String folderPathInBox, String[] uuids,
			boolean isStarred) throws Exception {
		for (String uuid : uuids)
			setStar(folderPathInBox, uuid, isStarred);
	}

	public void setStar(String folderPathInBox, String uuid, boolean isStarred)
			throws Exception {
		String oldPathInBox = this.findPathInBox(folderPathInBox, uuid);
		String mailName = new File(this.findPathInFs(folderPathInBox, uuid))
				.getName();
		if (isStarred && !mailName.startsWith(MAIL_FILE_PREFIX_STAR)) {
			String newPathInBox = folderPathInBox + "/starred" + mailName;
			if (!exists(newPathInBox)) {
				long time = (new File(oldPathInBox)).lastModified();
				this.mvCommonFile(oldPathInBox, newPathInBox);
				(new File(newPathInBox)).setLastModified(time);
				setUpdateFlag(folderPathInBox, true);
			}
		} else if (!isStarred && mailName.startsWith(MAIL_FILE_PREFIX_STAR)) {
			String newPathInBox = folderPathInBox + "/" + mailName.substring(7);
			if (!exists(newPathInBox)) {
				long time = (new File(oldPathInBox)).lastModified();
				this.mvCommonFile(oldPathInBox, newPathInBox);
				(new File(newPathInBox)).setLastModified(time);
				setUpdateFlag(folderPathInBox, true);
			}
		}
	}

	public List<MailBoxLsItem> lsStarMail() throws Exception {
		List<MailBoxLsItem>  lsMail = lsMail("inbox");
		List<MailBoxLsItem> list = new LinkedList<MailBoxLsItem>();
		for(MailBoxLsItem item: lsMail) 
			if (item.getStarred())
				list.add(item);
		return list;
	}

	public boolean isStarred(String folderPathInBox, String uuid) {
		String newPathInFs = findPathInFs(folderPathInBox, uuid);
		return isStarred(newPathInFs);

	}
	
	public boolean isStarred(String filename) {
		return (new File(filename)).getName().startsWith(MAIL_FILE_PREFIX_STAR);
	}

	public boolean isReply(String folderPathInBox, String uuid) {
		String newPathInFs = findPathInFs(folderPathInBox, uuid);
		return (new File(newPathInFs)).getName().endsWith(MAIL_FILE_SUFFIX_3);
	}

	public String getReplyMail() {
		if (!new File(this.replyMailPathInFs).exists())
			return "\r\n";
		else {
			String[] a;
			try {
				a = TextFile.loadLines(this.replyMailPathInFs);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < a.length; i++) {
					sb.append(a[i]).append("\r\n");
				}
				return sb.toString();
			} catch (IOException e) {
				return "\r\n";
			}

		}

	}

	public void saveReplyMail(String autoReplyC) throws Exception {
		TextFile.save(this.replyMailPathInFs, autoReplyC);
	}

	public void creatAlertMail(long usedSize, long spaceAlert) throws Exception {
		String alertMailUUID = (new UUID()).toShortString();
		String pathInFs = this.getPathInFs("inbox/" + alertMailUUID);
		if (AutoMailMaker.createAlertMail(pathInFs, usedSize, spaceAlert))
			this.setUpdateFlag("inbox", true);
		modSize(new File(pathInFs).length());
	}
	
	public MailFileBoxFolderInfo getInfo(String folderPathInBox) {
		String pathInFs = this.getPathInFs(folderPathInBox);
		File dir = new File(pathInFs);
		File[] files = dir.listFiles();
		
		int count = 0;
		int oldCount = 0;
		int starCount = 0;
		for(File file: files) {
			String fileName = file.getName();
			
			if (fileName.equals("cache.csv"))
				continue;
			if (fileName.equals("update"))
				continue;
			
			count ++;
			if (fileName.indexOf(MAIL_FILE_SUFFIX_S) > 0)
				oldCount ++;
			// 是否加星标
			if (isStarred(fileName)) 
				starCount ++;
		}
		
		MailFileBoxFolderInfo info = new MailFileBoxFolderInfo();
		info.setCount(count);
		info.setNewCount(count - oldCount);
		info.setStarCount(starCount);
		return info;
	}
	
	/**
	 * 邮件文件夹信息
	 */
	public class MailFileBoxFolderInfo {
		// 总邮件数(除“cache.csv”和“update”文件)
		private int count;
		
		// 新邮件数
		private int newCount;

		private int starCount;
		
		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getNewCount() {
			return newCount;
		}

		public void setNewCount(int newCount) {
			this.newCount = newCount;
		}

		public int getStarCount() {
			return starCount;
		}

		public void setStarCount(int starCount) {
			this.starCount = starCount;
		}
		
		
	}


	

	private Csv getClassCsv() throws IOException {
		Csv csv;
		if (!new File(this.mailClassPathInFs).exists())
			csv = new Csv(this.mailClassPathInFs, MailClass.labels, charset);
		else
			csv = new Csv(this.mailClassPathInFs, charset);
		return csv;
	}

	public void addClass(String mailClassName, String mailClassFolderName,
			String target, String op, String keyWord) throws IOException {
		Csv csv = getClassCsv();
		String[] values = { mailClassName, mailClassFolderName, target, op,
				keyWord };
		csv.insert(values);
	}

	public  List<MailClass>  getClasses() throws IOException {
		Csv csv = getClassCsv();
		ArrayList<String[]>  lineList = csv.getLineList();
		List<MailClass>  mailClasses = new LinkedList<MailClass>();
		for (String[] values: lineList) {
			mailClasses.add(new MailClass(values[0], values[1], values[2],
					values[3], values[4]));
		}
		return mailClasses;
	}

	public byte[] getClassesBytes() throws IOException {
		Csv csv = getClassCsv();
		return csv.toBytes();
	}

	public void rmMailClass(String mailClassName) throws IOException {
		Csv csv = getClassCsv();
		csv.delete(mailClassName);
	}


	@Override
	public void rmDir(String dirPathInBox) throws Exception {
		String pathInFs = getPathInFs(dirPathInBox);
		File dir = new File(pathInFs);
		File[] files = dir.listFiles();
		boolean isEmpty = true;
		for(File file: files) {
			String fn = file.getName();
			if (!fn.equals("cache.csv") && !fn.equals("update")) {
				isEmpty = false;
				throw new IOException("The dir is not empty, can't be removed.");
			}
		}
		if (isEmpty) {
			new File(pathInFs + "/cache.csv").delete();
			new File(pathInFs + "/update").delete();
			super.rmDir(dirPathInBox);
		}
	}
}
