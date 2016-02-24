package com.skymiracle.client.tcpClient.smtpClient;

import java.util.ArrayList;

public class SmtpClientLoadRunner {

	public static void main(String[] args) {

		if (args.length < 5) {
			System.out.println("usage:\n from to host curCount timesPerThread");
			return;
		}
		String from = args[0];
		String to = args[1];
		String host = args[2];
		int curCount = Integer.parseInt(args[3]);
		int timesPerThread = Integer.parseInt(args[4]);

		ArrayList<String> dataLineList = new ArrayList<String>();
		dataLineList.add("from: " + from);
		dataLineList.add("to: " + to);
		dataLineList.add("subject: test mail");
		dataLineList.add("");
		dataLineList.add("test mail content.");

		for (int i = 0; i < curCount; i++) {
			SendThread sendThread = new SendThread(from, to, host,
					dataLineList, timesPerThread);
			sendThread.start();
		}
	}
}
