package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class RsetCommander extends Pop3AbsCommander {

	public RsetCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		Mail[] mails = phStatus.getMails();
		for (int i = 0; i < mails.length; i++) 
			mails[i].setDeleted(false);
		return getBytesCRLF("+OK");
	}

}
