package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserDocEmptyFolderCommander extends UserDomainCommander {

	public UserDocEmptyFolderCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("folder isIncSub[true|false]");

		String folderPathInBox = args[0];
		boolean isIncSub = Boolean.parseBoolean(args[1]);

		getUsDocAccessorLocal(username, domain).docEmptyFolder(folderPathInBox,
				isIncSub);

		return getBytesCRLF("220 OK");
	}

}
