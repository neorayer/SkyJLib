package com.skymiracle.server.tcpServer;

/**
 * 连接频率检测线程
 */
public class TcpConnRateCheckThread extends Thread {
	TcpServer tcpServer;

	public TcpConnRateCheckThread(TcpServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.tcpServer.cleanConnRateTable();
				Thread.sleep(this.tcpServer.getConnRateSeconds() * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
