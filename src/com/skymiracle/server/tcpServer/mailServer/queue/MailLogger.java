package com.skymiracle.server.tcpServer.mailServer.queue;

public interface MailLogger {

	public void save(String sendTime, String mailType, String mailFrom, String rcptTo,
			long size, String result, String cause, String remark);
}
