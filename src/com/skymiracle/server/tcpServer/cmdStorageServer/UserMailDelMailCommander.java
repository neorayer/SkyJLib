package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailDelMailCommander extends UserDomainCommander {

	public UserMailDelMailCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 1)
			return getHelpBytesCRLF("folder uuid1|uuid2|uuid3|...");

		// folderPathInBox
		String folderPathInBox = args[0];

		// uuids
		String[] uuids = args[1].split("\\|");

		getUsMailAccessorLocal(username, domain).mailDelMail(folderPathInBox,
				uuids);

		return getBytesCRLF("220 OK");
	}

}
