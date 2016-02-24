package com.skymiracle.server.tcpServer.mailServer.Pop3;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.server.tcpServer.antiCracker.AntiCracker;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

/**
 * 把某个IP地址从ban中删去
 *
 */
public class AC_DeleCommander extends Pop3AbsCommander {

	public AC_DeleCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		AntiCracker ac = this.getPop3Server().getAntiCracker();
		
		String[] ss = tail.split(" ");
		if (ss.length != 2)
			return getBytesCRLF("-ERR Incorrect syntax.");
		
		String security = ss[0].trim();
		String ip = ss[1].trim();
		
		if (!ac.isAdminSecurityKeyCheck(security))
			return getBytesCRLF("-ERR Incorrect admin security key.");

		if (!ac.removeBan(ip))
			return getBytesCRLF("-ERR Failed to delete ip " + ip);

		return this.getBytesCRLF("+OK");
	}

}
