package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailNewFolderCommander extends UserDomainCommander {

	public UserMailNewFolderCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		if (tail.length() == 0)
			return getHelpBytesCRLF("newFolderName");

		String newFolderName = tail;

		getUsMailAccessorLocal(username, domain).mailNewFolder(newFolderName);

		return getBytesCRLF("220 OK");
	}

}
