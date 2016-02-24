package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 重置会话，当前传输被取消
 */
public class RsetCommander extends SmtpAbsCommander{

	public RsetCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		shStatus.reset();
		return getBytesCRLF("250 OK");
	}

}
