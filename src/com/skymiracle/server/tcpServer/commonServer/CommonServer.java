package com.skymiracle.server.tcpServer.commonServer;

import com.skymiracle.auth.AuthStorage;
import com.skymiracle.server.tcpServer.TcpServer;
import com.skymiracle.server.tcpServer.TcpConnHandler;

public abstract class CommonServer extends TcpServer {
	protected AuthStorage authStorage;

	public CommonServer(Class<? extends TcpConnHandler<?>> connHandlerClass) {
		super(connHandlerClass);
	}

	public CommonServer(String name, int port,
			Class<? extends TcpConnHandler<?>> connHandlerClass,
			AuthStorage authStorage) throws Exception {
		super(name, port, connHandlerClass);
		this.authStorage = authStorage;
	}

	public AuthStorage getAuthStorage() {
		return this.authStorage;
	}

	public void setAuthStorage(AuthStorage authStorage) {
		this.authStorage = authStorage;
	}

	public String getDefaultDomain() {
		return this.authStorage.getDefaultDomain();
	}

}
