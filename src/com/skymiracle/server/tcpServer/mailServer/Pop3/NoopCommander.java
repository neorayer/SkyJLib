package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class NoopCommander extends Pop3AbsCommander{

	public NoopCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF("+OK");
	}

}
