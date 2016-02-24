package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class DeleCommander extends Pop3AbsCommander {

	public DeleCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;
			int id = Integer.parseInt(tail) - 1;
			
			Mail[] mails = phStatus.getMails();
			if (id < 0 || id >= mails.length)
				return getBytesCRLF("-ERR Invalid message number.");
			
			Mail mail = mails[id];

			if (mail.isDeleted())
				return getBytesCRLF("-ERR message " + (id + 1) + " has been deleted");

			mail.setDeleted(true);
			
			return getBytesCRLF("+OK Deleted");
	}

}
