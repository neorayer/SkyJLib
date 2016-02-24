package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 *	结束会话 
 */
public class QuitCommander extends SmtpAbsCommander{

	public QuitCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		setQuiting(true);
		return getBytesCRLF("221 Service closing transmission channel");
	}

}
