package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class QuitCommander extends Pop3AbsCommander{

	public QuitCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		phStatus.doDelete();
		setQuiting(true);
		return getBytesCRLF("+OK bye");
	}

}
