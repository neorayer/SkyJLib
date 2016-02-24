package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 用于验证指定的用户/邮箱是否存在；由于安全方面的原因，服务器常禁止此命令 
 */
public class VrfyCommander extends SmtpAbsCommander{

	public VrfyCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF("250 OK");
	}

}
