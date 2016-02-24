package com.skymiracle.server.tcpServer.mailServer.queue;

import java.io.File;
import java.io.IOException;

public class TimeQueueItem {

	private String uuid;
	private long beginTimeStamp;
	private String fromusername;
	private String fromdomain;
	private String toStrs;
	private String location;

	public TimeQueueItem(String path) {
		try {
			MailMessage message = new MailMessage(path, false);
			this.fromusername = message.getFromUsername();
			this.fromdomain = message.getFromDomain();
			this.toStrs = message.getToStr();
			this.location = message.getTargetLocation();
			this.beginTimeStamp = Long.parseLong(path.substring(path
					.lastIndexOf("_") + 1));
			String name = new File(path).getName();
			this.uuid = name.substring(0, name.indexOf("_"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TimeQueueItem(String uuid, MailMessage message, long beginTimeStamp,
			String rootPath) {
		this.uuid = uuid;
		this.beginTimeStamp = beginTimeStamp;
		this.fromusername = message.getFromUsername();
		this.fromdomain = message.getFromDomain();
		this.toStrs = message.getToStr();
		this.location = message.getTargetLocation();
		try {
			message.save(rootPath + "/" + uuid + "_" + beginTimeStamp);
		} catch (IOException e) {

		}
	}

	public void remove(String rootPath) throws IOException {
		if (!new File(rootPath + "/" + this.uuid + "_" + this.beginTimeStamp
				+ ".props").delete())
			throw new IOException("Can not remote MailMessage propfile");
		if (!new File(rootPath + "/" + this.uuid + "_" + this.beginTimeStamp)
				.delete())
			throw new IOException("Can not remote MailMessage datafile");
	}

	public long getBeginTimeStamp() {
		return this.beginTimeStamp;
	}

	public String getFromdomain() {
		return this.fromdomain;
	}

	public String getFromusername() {
		return this.fromusername;
	}

	public String getLocation() {
		return this.location;
	}

	public String getToStrs() {
		return this.toStrs;
	}

	public String getUUID() {
		return this.uuid;
	}
}
