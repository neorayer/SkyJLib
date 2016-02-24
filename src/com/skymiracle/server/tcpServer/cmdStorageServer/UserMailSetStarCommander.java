package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailSetStarCommander extends UserDomainCommander {

	public UserMailSetStarCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 3)
			return getHelpBytesCRLF("folder uuid1|uuid2|uuid3|... [true|false]");

		// folderPathInBox
		String folderPathInBox = args[0];

		// uuids
		String[] uuids = args[1].split("\\|");

		// isStar
		boolean isStar = Boolean.parseBoolean(args[2]);

		getUsMailAccessorLocal(username, domain).mailSetStar(folderPathInBox,
				uuids, isStar);

		return getBytesCRLF("220 OK");
	}

}
