package com.skymiracle.server;

public interface Server extends Runnable {
	public String getName();

	public void start();

	public void stop();

	public String execCmd(String serverCmd);

	public ServerInfo getServerInfo();

}
