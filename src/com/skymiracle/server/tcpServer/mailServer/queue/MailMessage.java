package com.skymiracle.server.tcpServer.mailServer.queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.skymiracle.io.TextFile;

public class MailMessage {
	private String fromUsername;

	private String fromDomain;

	private String toStr;

	private String targetLocation;

	private List<String> dataLineList;

	private String missionUUID;
	
	private long size;

	private long genTime = 0L;

	private int delayLevel = 0;

	public MailMessage(String fromUsername, String fromDomain, String toStr,
			String targetLocation, List<String> dataLineList, String missionUUID) {
		this.fromUsername = fromUsername;
		this.fromDomain = fromDomain;
		this.toStr = toStr;
		this.targetLocation = targetLocation;
		this.dataLineList = dataLineList;
		this.missionUUID = missionUUID;
		this.genTime = System.currentTimeMillis();
	}

	/**
	 * get the message content from the store file head content is got from the
	 * id.props body content is got from the id.data
	 * 
	 * @param propsFilePath
	 * @throws IOException
	 */
	public MailMessage(String dataFilePath) throws IOException {
		this(dataFilePath, true);
	}

	public void loadSource(String dataFilePath) throws IOException {
		this.dataLineList = TextFile.loadLinesList(dataFilePath);
	}

	public MailMessage(String dataFilePath, boolean isLoadSource)
			throws IOException {
		String propsFilePath = dataFilePath + ".props";
		Properties props = new Properties();
		FileInputStream fis;
		fis = new FileInputStream(propsFilePath);
		props.load(fis);
		fis.close();
		this.fromUsername = props.getProperty("fromUsername");
		this.fromDomain = props.getProperty("fromDomain");
		this.toStr = props.getProperty("toStr");
		this.targetLocation = props.getProperty("targetLocation");
		this.missionUUID = props.getProperty("uuid");
		this.genTime = Long
				.parseLong(props.getProperty("genTime") == null ? "0" : props
						.getProperty("genTime"));
		this.delayLevel = Integer
				.parseInt(props.getProperty("delayLevel") == null ? "0" : props
						.getProperty("delayLevel"));
		if (isLoadSource)
			this.dataLineList = TextFile.loadLinesList(dataFilePath,  "ISO-8859-1");
	}

	/**
	 * save the message content into the store file head content is saved into
	 * the id.props body content is saved into the id.data
	 * 
	 * @param dirPath
	 * @throws IOException
	 */
	public void save(String dataFilePath) throws IOException {
		Properties props = new Properties();
		props.setProperty("fromUsername", this.fromUsername);
		props.setProperty("fromDomain", this.fromDomain);
		props.setProperty("toStr", this.toStr);
		props.setProperty("targetLocation", this.targetLocation);
		props.setProperty("uuid", this.missionUUID);
		props.setProperty("genTime", Long.toString(this.genTime));
		props.setProperty("delayLevel", Integer.toString(this.delayLevel));
		FileOutputStream fos;
		fos = new FileOutputStream(new StringBuffer(dataFilePath).append(
				".props").toString());
		props.store(fos, "");
		fos.close();

		TextFile.save(dataFilePath, this.dataLineList, "ISO-8859-1");
	}

	public static void remove(String dataFilePath) throws IOException {
		if (!new File(dataFilePath + ".props").delete())
			throw new IOException(
					"Can not remote MailMessage propfile, dataFilePath="
							+ dataFilePath);
		if (!new File(dataFilePath).delete())
			throw new IOException(
					"Can not remote MailMessage datafile, dataFilePath="
							+ dataFilePath);
	}

	@Override
	public String toString() {
		return new StringBuffer().append(" fromUsername=").append(
				this.fromUsername).append(" fromDomain=").append(
				this.fromDomain).append(" toStr=").append(this.toStr).append(
				" uuid=").append(this.missionUUID).toString();
	}

	public List<String> getDataLineList() {
		return this.dataLineList;
	}

	public String getFromDomain() {
		return this.fromDomain;
	}

	public String getFromUsername() {
		return this.fromUsername;
	}

	public String getTargetLocation() {
		return this.targetLocation;
	}

	public String getToStr() {
		return this.toStr;
	}

	public long getSize() {
		if(size > 0)
			return size;
		
		if(this.dataLineList == null)
			return 0;
		
		for(String line: this.dataLineList) {
			size = size + line.length();
		}
		
		return size;
		
//		这段代码有BUG。
//		long c = 0;
//		for (int i = 0; i < this.dataLineList.size(); i++)
//			c += (this.dataLineList.get(i)).length();
//		return c;
	}

	public String getMissionUUID() {
		return this.missionUUID;
	}

	public long getGenTime() {
		return this.genTime;
	}

	public int getDelayLevel() {
		return this.delayLevel;
	}

	public void setDelayLevel(int delayLevel) {
		this.delayLevel = delayLevel;
	}

	public void setGenTime(long genTime) {
		this.genTime = genTime;
	}

}
