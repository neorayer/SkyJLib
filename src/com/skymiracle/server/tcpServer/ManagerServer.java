package com.skymiracle.server.tcpServer;

import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.ServerManager;

public class ManagerServer extends TcpServer {
	private String username;

	private String password;

	private ServerManager serverManager;

	public ManagerServer(ServerManager serverManager, int port,
			String username, String password) throws Exception {
		super("MANAGER", port, ManagerServerConnHandler.class);
		this.serverManager = serverManager;
		this.username = username;
		this.password = password;
		this.defaultCharset = "ISO8859-1";
	}

	public boolean auth(String inUsername, String inPassword) {
		if (this.username.equals(inUsername)
				&& this.password.equals(inPassword))
			return true;
		return false;
	}

	public ServerManager getManager() {
		return this.serverManager;
	}

	public Object getServersInfo() {
		return this.serverManager.getServersInfo();
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		return new TcpServerInfo();
	}

}
