package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserDocDelFilesCommander extends UserDomainCommander {

	public UserDocDelFilesCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 1)
			return getHelpBytesCRLF(" filepath1|filepath2|filepath3|...");

		// filepaths
		String[] fps = args[0].split("\\|");

		getUsDocAccessorLocal(username, domain).docDelFiles(
				fps);

		return getBytesCRLF("220 OK");
	}

}
