package com.skymiracle.client.tcpClient.smtpClient;

import java.util.ArrayList;

public class SendThread extends Thread {

	private ArrayList<String> dataLineList;

	private String from;

	private String to;

	private int times;

	private String host;

	public SendThread(String from, String to, String host,
			ArrayList<String> dataLineList, int times) {
		this.from = from;
		this.to = to;
		this.dataLineList = dataLineList;
		this.host = host;
		this.times = times;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < this.times; i++) {
				SmtpClient smtpClient = new SmtpClient();
				smtpClient.setFromEmail(this.from);
				smtpClient.send(this.host, 25, new String[] { this.to },
						this.dataLineList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
