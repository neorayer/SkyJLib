package com.skymiracle.server;

import java.io.Serializable;

public class ServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6878446627680540242L;

	private String serverName;

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
