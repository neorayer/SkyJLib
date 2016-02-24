package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailLsMailUUIDsizeCommander extends UserDomainCommander {

	public UserMailLsMailUUIDsizeCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		if (tail.length() == 0)
			return getHelpBytesCRLF("folderPathInBox");

		String folderPathInBox = tail.trim();
		String[] ss = getUsMailAccessorLocal(username, domain)
				.mailLsMailUUIDsize(folderPathInBox);

		println("220 OK");
		readln();

		StringBuffer sb = new StringBuffer();
		for (String s : ss) {
			sb.append(s).append("\r\n");
		}
		return getBytes(sb.toString());
	}

}
