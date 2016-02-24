package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 向服务器标识用户身份
 */
public class HeloCommander extends SmtpAbsCommander{

	public HeloCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		String helloName = tail.trim();
		
		shStatus.setHeloType(SmtpHandleStatus.HELLO_TYPE_HELO);
		shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_HELO);
		shStatus.setHeloName(helloName);

		return getBytesCRLF("250 "+ getSmtpServer().getHeloHostname() + " RemoteIP:" + getRemoteIP());

	}

}
