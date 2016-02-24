package com.skymiracle.server;

import java.io.Serializable;

public class ServersInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4375549066386933411L;

	private ServerInfo[] serverInfos = new ServerInfo[0];

	public ServerInfo[] getServerInfos() {
		return this.serverInfos;
	}

	public void setServerInfos(ServerInfo[] serverInfos) {
		this.serverInfos = serverInfos;
	}

}
