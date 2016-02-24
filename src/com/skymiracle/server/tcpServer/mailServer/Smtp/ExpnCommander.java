package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 验证给定的邮箱列表是否存在，扩充邮箱列表，常被禁用 
 */
public class ExpnCommander extends SmtpAbsCommander{

	public ExpnCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		return getBytesCRLF("250 OK");
	}

}
