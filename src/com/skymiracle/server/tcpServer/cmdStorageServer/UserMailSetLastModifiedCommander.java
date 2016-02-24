package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailSetLastModifiedCommander extends UserDomainCommander {

	public UserMailSetLastModifiedCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 3)
			return getHelpBytesCRLF("folder uuid lastmodified");

		// folderPathInBox
		String folderPathInBox = args[0];

		// uuid
		String uuid = args[1];

		// isLastModified
		long isLastModified = Long.parseLong(args[2]);

		getUsMailAccessorLocal(username, domain).mailSetLastModified(
				folderPathInBox, uuid, isLastModified);

		return getBytesCRLF("220 OK");
	}

}
