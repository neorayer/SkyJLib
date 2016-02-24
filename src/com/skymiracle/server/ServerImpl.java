package com.skymiracle.server;

import com.skymiracle.logger.Loggable;
import com.skymiracle.logger.SimpleAppLogger;

public abstract class ServerImpl extends SimpleAppLogger implements Loggable, Server {
	//  服务器名称
	protected String name = "NoName";

	protected Loggable logger;
	
	public ServerImpl() {
	}

	public ServerImpl(String name) {
		this.name = name;
	}

	public Loggable getLogger() {
		return logger;
	}

	public void setLogger(Loggable logger) {
		this.logger = logger;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String execCmd(String serverCmd) {
		return null;
	}

	public ServerInfo getServerInfo() {
		ServerInfo serverInfo = newServerInfoInstance();
		serverInfo.setServerName(this.name);
		return serverInfo;
	}

	protected abstract ServerInfo newServerInfoInstance();
	
	

}
