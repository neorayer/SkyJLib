package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

public class ListCommander extends Pop3AbsCommander {

	public ListCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_PASS)
			return null;

		StringBuffer sb = new StringBuffer();
		Mail[] mails = phStatus.getMails();
		int totalSize = 0;
		int count = 0;
		for (int i = 0; i < mails.length; i++) {
			if (mails[i].isDeleted())
				continue;
			sb.append(i + 1).append(" ").append(mails[i].getSize()).append(
					"\r\n");
			count ++;
			totalSize += mails[i].getSize();
		}
		sb.append(".");
		StringBuffer resSb = new StringBuffer();
		resSb.append("+OK " + count + " " +totalSize +"\r\n");
		resSb.append(sb);
		return getBytesCRLF(resSb.toString());
	}

}
