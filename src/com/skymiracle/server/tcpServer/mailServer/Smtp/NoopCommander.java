package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 无操作，服务器应响应OK
 */
public class NoopCommander extends SmtpAbsCommander{

	public NoopCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF("250 OK. Powered by www.skymiracle.com");
	}

}
