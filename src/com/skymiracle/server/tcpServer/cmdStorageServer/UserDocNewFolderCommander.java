package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
/**
 * 文件夹创建命令
 */
public class UserDocNewFolderCommander extends UserDomainCommander {

	public UserDocNewFolderCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 1)
			return getHelpBytesCRLF("folderPathInBox newFolderName");
		String folderPathInBox = args[0];
		String newFolderName = args[1];

		getUsDocAccessorLocal(username, domain).docNewFolder(folderPathInBox,
				newFolderName);

		return getBytesCRLF("220 OK");
	}

}
