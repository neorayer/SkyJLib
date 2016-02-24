package com.skymiracle.fileBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.skymiracle.csv.Csv;
import com.skymiracle.mime.AttachmentFinder;
import com.skymiracle.mime.MimeHeader;
import com.skymiracle.mime.MimeHeaderImpl;

public class MailBoxLsReaderCacheCsvImpl {

	private MailFileBox mailBox;

	private String pathInBox;

	private static final String cacheFileName = "cache.csv";
	
	private String cachePathInFs;

	private static String updateFlagName = "update";

	private List<FileInfo> fileInfos;

	private HashMap<String, String> fsMap, cacheMap;

	private ArrayList<String[]> cacheLineList;

	private Csv cacheCsv;
	
	private String charset = "UTF-8";

	private static Map<String, Object> csvSyncLockMap = new ConcurrentHashMap<String,Object>();
	
	public MailBoxLsReaderCacheCsvImpl(MailFileBox mailBox, String pathInBox)
			throws Exception {
		this.mailBox = mailBox;
		this.pathInBox = pathInBox;
		this.cachePathInFs = this.mailBox.getPathInFs(pathInBox) + "/"
				+ cacheFileName;
		this.fileInfos = this.mailBox.lsFileInfos(pathInBox);

		this.fsMap = new HashMap<String, String>();
		for (FileInfo fileInfo: this.fileInfos) {
			String uuid = MailFileBox.fileNameToUUID(fileInfo
					.getName());
			this.fsMap.put(uuid, "");
		}

		File cacheFile = new File(this.cachePathInFs);
		if (!cacheFile.exists()) {
			this.cacheCsv = new Csv(this.cachePathInFs, MailBoxLsItem.csvLabels, charset);
		}
		this.cacheCsv = new Csv(this.cachePathInFs,  charset);
		this.cacheLineList = this.cacheCsv.getLineList();

		this.cacheMap = new HashMap<String, String>();
		for (int i = 0; i < this.cacheLineList.size(); i++) {
			String[] values = this.cacheLineList.get(i);
			this.cacheMap.put(values[0], "");
		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public List<MailBoxLsItem>  lsMail() throws Exception {
		syncCache();
		List<MailBoxLsItem> lsItems = new LinkedList<MailBoxLsItem>();
		for (int i = 0; i < this.cacheLineList.size(); i++)
			lsItems.add(new MailBoxLsItem(this.cacheLineList.get(i)));
		return lsItems;
	}

	public byte[] lsMailCsvBytes() throws Exception {
		syncCache();
		byte[] bytes = this.cacheCsv.toBytes();
		return bytes;
	}

	public byte[] lsMailCsvBytes(int lineCount) throws Exception {
		syncCache();
		byte[] bytes = this.cacheCsv.toBytes(lineCount);
		return bytes;
	}

	private void syncCache() throws Exception {
		//找到该Csv文件的锁
		Object lock = csvSyncLockMap.get(this.cachePathInFs);
		if (lock == null) {
			//如果没找到锁，生成新锁，放入Map中
			lock = new Object();
			csvSyncLockMap.put(this.cachePathInFs, lock);
		}
		synchronized (lock){
			
			if (!this.mailBox.isUpdate(this.pathInBox))
				return;
			ArrayList<String[]> newList = new ArrayList<String[]>();
			for (int i = 0; i < this.cacheLineList.size(); i++) {
				String[] value = this.cacheLineList.get(i);
				if (this.fsMap.get(value[0]) == null)
					continue;
				// old csv
				String mailPathInFs = this.mailBox.findPathInFs(this.pathInBox,
						value[0]);
				FileInfo fi = new FileInfo(mailPathInFs, this.mailBox
						.findPathInBox(this.pathInBox, value[0]));
				if (value.length != 12) {
					String[] tmp = new String[12];
					for (int x = 0; x < 9; x++)
						tmp[x] = value[x];
					tmp[7] = mailPathInFs.endsWith(",S") ? "1" : "0";
					tmp[8] = fi.getName().startsWith("starred") ? "1" : "0";
					tmp[9] = AttachmentFinder.findAttachment(mailPathInFs) ? "1"
							: "0";
					tmp[10] = mailPathInFs.endsWith("!3,S") ? "1" : "0";
					tmp[11] = "3";
					newList.add(tmp);
				} else {
					value[7] = mailPathInFs.endsWith(",S") ? "1" : "0";
					value[8] = fi.getName().startsWith("starred") ? "1" : "0";
					value[10] = mailPathInFs.endsWith("!3,S") ? "1" : "0";
					newList.add(value);
				}
	
			}
	
			// recreate cacheLineList
			this.cacheLineList.clear();
			this.cacheLineList.addAll(newList);
	
			for (FileInfo fileInfo: this.fileInfos) {
				if (fileInfo.isDirectory())
					continue;
				String fileName = fileInfo.getName();
				if (fileName.equals(cacheFileName))
					continue;
				if (fileName.equals(updateFlagName))
					continue;
				String uuid = MailFileBox.fileNameToUUID(fileName);
				if (this.cacheMap.get(uuid) == null) {
					MimeHeader mimeHeader = new MimeHeaderImpl(fileInfo
							.getAbsolutePath());
					MailBoxLsItem mailInfo = new MailBoxLsItem(uuid, mimeHeader,
							fileInfo);
					String[] values = mailInfo.getCsvValues();
					this.cacheLineList.add(values);
				}
			}
	
			this.cacheCsv.save();
			this.mailBox.setUpdateFlag(this.pathInBox, false);
		}
		
		//删除锁
		csvSyncLockMap.remove(this.cachePathInFs);
	}

	public class MyComparator implements Comparator<String[]> {
		private int col;

		public MyComparator(int i) {
			this.col = i;
		}

		public int compare(String[] valueLine1,String[] valueLine2) {
			return valueLine1[this.col].compareTo(valueLine2[this.col]);
		}

	}

	public static void main(String[] args) throws Exception {
		String path = "/wpx/mstorage/ceshi.com/9/90/sky/mailbox/";

		MailFileBox mb = new MailFileBox(path);
		MailBoxLsReaderCacheCsvImpl mbr = new MailBoxLsReaderCacheCsvImpl(mb,
				"inbox");
		mbr.syncCache();
	}
}
