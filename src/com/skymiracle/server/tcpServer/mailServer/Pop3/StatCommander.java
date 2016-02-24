package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class StatCommander extends Pop3AbsCommander {

	public StatCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		Mail[] mails = phStatus.getMails();
		long size = phStatus.getMailBoxSize();

		return getBytesCRLF("+OK " + mails.length + " " + size);
	}

}
