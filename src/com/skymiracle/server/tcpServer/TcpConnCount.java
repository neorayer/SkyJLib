package com.skymiracle.server.tcpServer;

/**
 *	某个IP的连接频率
 */
public class TcpConnCount {

	private int count;

	private String ip;

	public TcpConnCount(String ip) {
		this.ip = ip;
		this.count = 0;
	}

	public void inc() {
		this.count++;
	}

	public void dec() {
		this.count--;
	}

	public String getIp() {
		return this.ip;
	}

	public int getCount() {
		return this.count;
	}

	@Override
	public String toString() {
		return this.ip + "\t" + this.count;
	}
}
