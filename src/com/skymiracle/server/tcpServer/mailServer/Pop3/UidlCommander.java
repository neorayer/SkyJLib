package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class UidlCommander extends Pop3AbsCommander {

	public UidlCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		Mail[] mails = phStatus.getMails();
		int count = 0;
		int size = 0;
		if (tail.trim().length() == 0) {
			StringBuffer sb = new StringBuffer();
			for (int id = 0; id < mails.length; id++) {
				Mail mail = mails[id];
				if (mail.isDeleted())
					continue;
				sb.append("" + (id + 1)).append(" ").append(mail.getUuid())
						.append("\r\n");
				count ++;
				size += mail.getSize();
			}
			sb.append(".");

			StringBuffer resSb = new StringBuffer();
			resSb.append("+OK " + count +" " + size).append("\r\n");
			resSb.append(sb);
			
			return getBytesCRLF(resSb.toString());
		} else {
			int id = Integer.parseInt(tail) - 1;
			if (id < 0 || id >= mails.length)
				return getBytesCRLF("-ERR Invalid message number.");

			Mail mail = mails[id];

			if (mail.isDeleted())
				return getBytesCRLF("-ERR message " + (id + 1)
						+ " has been deleted");

			return getBytesCRLF("+OK " + (id + 1) + " " + mail.getUuid());
		}
	}
}
