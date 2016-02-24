package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserDocMoveFilesToCommander extends UserDomainCommander {

	public UserDocMoveFilesToCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("srcFolder filepath1|filepath2|filepath3|... destFolder");


		// filepaths
		String[] filepaths = args[0].split("\\|");

		// destFolderPathInBox
		String destFolderPathInBox = args[1];

		getUsDocAccessorLocal(username, domain).docMoveFilesTo(
				filepaths, destFolderPathInBox);

		return getBytesCRLF("220 OK");
	}

}
