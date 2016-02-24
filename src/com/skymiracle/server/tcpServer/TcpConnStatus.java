package com.skymiracle.server.tcpServer;

/**
 * TCP客户端连接情况
 */
public class TcpConnStatus {

	private String id;

	private String remoteIP;

	private long connTimestamp;

	public TcpConnStatus(String id, String remoteIP, long connTimestamp) {
		super();
		this.id = id;
		this.remoteIP = remoteIP;
		this.connTimestamp = connTimestamp;
	}

	public long getConnTimestamp() {
		return this.connTimestamp;
	}

	public String getId() {
		return this.id;
	}

	public String getRemoteIP() {
		return this.remoteIP;
	}

}
