package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailEmptyFldrCommander extends UserDomainCommander {

	public UserMailEmptyFldrCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		if (tail.trim().length() == 0)
			return getHelpBytesCRLF("folderName");

		String folderName = tail.trim();
		getUsMailAccessorLocal(username, domain).mailEmptyFldr(folderName);

		return getBytesCRLF("220 OK");
	}

}
