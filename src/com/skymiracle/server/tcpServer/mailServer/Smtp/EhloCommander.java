package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 向服务器标识用户身份
 */
public class EhloCommander extends SmtpAbsCommander{

	public EhloCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		String helloName = tail.trim();

		shStatus.setHeloType(SmtpHandleStatus.HELLO_TYPE_EHLO);
		shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_EHLO);
		shStatus.setHeloName(helloName);
		
		StringBuffer sb = new StringBuffer("250-").append(
				getSmtpServer().getHeloHostname()).append("\r\n").append(
				"250-PIPELINING\r\n").append("250-SIZE ").append(getSmtpServer().getMaxMessageSize())
				.append("\r\n").append("250-VRFY\r\n").append("250-EXPN\r\n")
				.append("250-AUTH LOGIN PLAIN\r\n").append("250 8BITMIME");

		
		return getBytesCRLF(sb.toString());

	}

}
