package com.skymiracle.server;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.ManagerServer;
import com.skymiracle.server.tcpServer.TcpServer;

public class ServerManagerImpl implements ServerManager {

	private ManagerServer mgrServer;

	private List<Server> serverList = new LinkedList<Server>();

	public ServerManagerImpl(int mgrSrvPort, String mgrSrvUsername,
			String mgrSrvPassword) throws Exception {
		super();
		this.mgrServer = new ManagerServer(this, mgrSrvPort, mgrSrvUsername,
				mgrSrvPassword);
		addServer(this.mgrServer);
	}

	public ServerManagerImpl() {
		super();
	}

	public ManagerServer getMgrServer() {
		return this.mgrServer;
	}

	public void setMgrServer(ManagerServer mgrServer) {
		this.mgrServer = mgrServer;
		addServer(mgrServer);
	}

	public void addServer(Server server) {
		this.serverList.add(server);
	}

	public TcpServer getManageServer() {
		return this.mgrServer;
	}

	public void startMgrServer() {
		startServer(this.mgrServer.getName());
	}

	public void startAllServers() {
		for (Server server : this.serverList) {
			server.start();
		}
	}

	public void startServer(String name) {
		for (Server server : this.serverList) {
			if (server.getName().equals(name)) {
				server.start();
				return;
			}
		}

		Logger.warn(new StringBuffer("Can't start server:").append(name)
				.append(" - Server not been found."));
	}

	public void stopServer(String name) {
		for (Server server : this.serverList) {
			server.stop();
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Server server : this.serverList) {
			sb.append(server.toString()).append("\r\n");
		}
		return sb.toString();
	}

	public String execCmd(String cmd) {
		int index = cmd.indexOf(" ");
		String sHead = cmd.substring(0, index);
		String sTail = cmd.substring(index + 1);
		String serverName = sHead.trim();
		String serverCmd = sTail.trim();

		for (Server server : this.serverList) {
			if (server.getName().equalsIgnoreCase(serverName))
				return server.execCmd(serverCmd);
		}

		return null;
	}

	public ServersInfo getServersInfo() {
		Server[] servers = this.serverList.toArray(new Server[0]);
		ServerInfo[] serverInfos = new ServerInfo[servers.length];
		for (int i = 0; i < servers.length; i++)
			serverInfos[i] = servers[i].getServerInfo();
		ServersInfo serversInfo = new ServersInfo();
		serversInfo.setServerInfos(serverInfos);
		return serversInfo;
	}

}
