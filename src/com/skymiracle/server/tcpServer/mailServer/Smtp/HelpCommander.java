package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 查询服务器支持什么命令
 */
public class HelpCommander extends SmtpAbsCommander{

	public HelpCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF("250 OK. Powered by www.skymiracle.com");
	}

}
