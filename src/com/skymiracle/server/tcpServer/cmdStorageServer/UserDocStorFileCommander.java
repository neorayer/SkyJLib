package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserDocStorFileCommander extends UserDomainCommander {

	public UserDocStorFileCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("folder filename");

		String folderPathInBox = args[0];
		String fileName = args[1];

		println("220 OK");

		getUsDocAccessorLocal(username, domain).docStorFile(folderPathInBox,
				fileName, getSocketInputStream(), true);

		return getBytesCRLF("220 OK");
	}

}
