package com.skymiracle.server.tcpServer;

import com.skymiracle.server.ServerInfo;

public class TcpServerInfo extends ServerInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5698507263277338614L;

	private int connCount;

	private boolean listening;

	private long millStartTime;

	public int getConnCount() {
		return this.connCount;
	}

	public void setConnCount(int connCount) {
		this.connCount = connCount;
	}

	public boolean isListening() {
		return this.listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public long getMillStartTime() {
		return this.millStartTime;
	}

	public void setMillStartTime(long millStartTime) {
		this.millStartTime = millStartTime;
	}

}
