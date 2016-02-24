package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailLsMailCommander extends UserDomainCommander {

	public UserMailLsMailCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		if (tail.length() == 0)
			return getHelpBytesCRLF("folderPathInBox");

		String folderPathInBox = tail.trim();
		byte[] bytes = getUsMailAccessorLocal(username, domain)
				.mailLsMailsBytes(folderPathInBox);

		println("220 OK");
		readln();

		return bytes;
	}

}
