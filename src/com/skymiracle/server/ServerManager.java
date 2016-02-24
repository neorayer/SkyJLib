package com.skymiracle.server;

import java.io.IOException;

public interface ServerManager {

	public void addServer(Server server);

	public void startMgrServer() throws IOException;

	public void startServer(String name) throws IOException;

	public void stopServer(String name);

	public String toString();

	public String execCmd(String cmd);

	public ServersInfo getServersInfo();

	public void startAllServers();
}