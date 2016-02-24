package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.Commander;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public abstract class AbsStorageCommander extends Commander {

	public AbsStorageCommander(CmdConnHandler connHandler) {
		super(connHandler);
		this.storageServer = (CmdStorageServer) this.connHandler.getCmdServer();
	}

	protected CmdStorageServer storageServer;

}
