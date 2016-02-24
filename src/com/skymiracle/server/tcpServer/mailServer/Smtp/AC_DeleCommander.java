package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.ArrayList;

import com.skymiracle.server.tcpServer.antiCracker.AntiCracker;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 向服务器标识用户身份
 */
public class AC_DeleCommander extends SmtpAbsCommander {

	public AC_DeleCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		AntiCracker ac = this.getSmtpServer().getAntiCracker();

		String[] ss = tail.split(" ");
		if (ss.length != 2)
			return getBytesCRLF("500 Incorrect syntax.");

		String security = ss[0].trim();
		String ip = ss[1].trim();

		if (!ac.isAdminSecurityKeyCheck(security))
			return getBytesCRLF("500 Incorrect admin security key.");

		if (!ac.removeBan(ip))
			return getBytesCRLF("500 Failed to delete ip " + ip);

		return this.getBytesCRLF("250 OK");
	}

}
