package com.skymiracle.server.tcpServer.cmdServer.testCase;

import com.skymiracle.server.tcpServer.cmdServer.Commander;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class EchoCommander extends Commander {

	public EchoCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF(tail);
	}

}
