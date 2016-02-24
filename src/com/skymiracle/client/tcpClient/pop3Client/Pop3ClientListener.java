package com.skymiracle.client.tcpClient.pop3Client;

public interface Pop3ClientListener {

	void connect();

	void sendUser();

	void sendPass();

	void sendList();

	void receivedMailCount(int c);

	void retrMail(String id, int mailSize);

	void quit();

	void receivedAllMailSize(long allMailSize);

	void mailRetring(String id, int size);

}
