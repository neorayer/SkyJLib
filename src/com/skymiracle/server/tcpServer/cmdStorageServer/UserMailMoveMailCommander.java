package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailMoveMailCommander extends UserDomainCommander {

	public UserMailMoveMailCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 3)
			return getHelpBytesCRLF("srcFolder uuid1|uuid2|uuid3|... destFolder");

		// srcFolderPathInBox
		String srcFolderPathInBox = args[0];

		// filenames
		String[] uuids = args[1].split("\\|");

		// destFolderPathInBox
		String destFolderPathInBox = args[2];

		getUsMailAccessorLocal(username, domain).mailMoveMail(
				srcFolderPathInBox, uuids, destFolderPathInBox);

		return getBytesCRLF("220 OK");
	}

}
