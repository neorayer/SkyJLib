package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class TopCommander extends Pop3AbsCommander {

	public TopCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		String[] idStrs = tail.split(" ");
		if (idStrs.length < 2)
			return getBytesCRLF("-ERR Invalid command");

		int[] ids = new int[2];
		ids[0] = Integer.parseInt(idStrs[0]) - 1;
		ids[1] = Integer.parseInt(idStrs[1]);

		Mail[] mails = phStatus.getMails();

		if (ids[0] < 0 || ids[0] >= mails.length)
			return getBytesCRLF("-ERR Invalid message number.");

		Mail mail = mails[ids[0]];
		if (mail.isDeleted())
			return getBytesCRLF("-ERR message " + (ids[0] + 1)
					+ " has been deleted");

		println("+OK");
		flush();

		if (ids[1] == 0)
			println(phStatus.getMailHeader(mail.getUuid()).toString());
		else
			println(phStatus.getMailBody(mail.getUuid(), ids[1]).toString());

		return getBytesCRLF("\r\n.");
	}
}
