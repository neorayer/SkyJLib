package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 文件夹删除命令
 */
public class UserDocDelFolderCommander extends UserDomainCommander {

	public UserDocDelFolderCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String folderPathInBox = tail.trim();
		if (folderPathInBox.length() < 1)
			return getHelpBytesCRLF("folder");

		getUsDocAccessorLocal(username, domain).docDelFolder(folderPathInBox);

		return getBytesCRLF("220 OK");
	}

}
