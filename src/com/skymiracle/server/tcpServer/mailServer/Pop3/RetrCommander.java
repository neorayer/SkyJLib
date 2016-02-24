package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class RetrCommander extends Pop3AbsCommander {

	public RetrCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		Mail[] mails = phStatus.getMails();
		int id = Integer.parseInt(tail) - 1;
		if (id < 0 || id >= mails.length)
			return getBytesCRLF("-ERR Invalid message number.");

		Mail mail = mails[id];

		if (mail.isDeleted())
			return getBytesCRLF("-ERR message " + (id + 1)
					+ " has been deleted");

		println("+OK");
		flush();

		String mailAccessPath = phStatus.getUsMailAccessor().mailRetr("inbox",
				mail.getUuid());

		if (mailAccessPath == null)
			return getBytesCRLF("-ERR sofeware native error.");

		StreamPipe.fileToOutput(mailAccessPath, getSocketOutputStream(), false);
		phStatus.getUsMailAccessor().mailSetRead("inbox",
				new String[] { mail.getUuid() }, true);

		getPop3Server().getPop3Logger().retrBegin(mail.getUuid(),
				phStatus.getUsername(), mail.getSize(), getRemoteIP());

		return getBytesCRLF("\r\n.");
	}

}
