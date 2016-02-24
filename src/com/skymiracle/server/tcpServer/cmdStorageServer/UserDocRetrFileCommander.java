package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.io.StreamPipe;

public class UserDocRetrFileCommander extends UserDomainCommander {

	public UserDocRetrFileCommander(CmdConnHandler connHandler) {
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
		readln();

		String fpath = getUsDocAccessorLocal(username, domain).docRetrFile(
				folderPathInBox, fileName);
		StreamPipe.fileToOutput(fpath, getSocketOutputStream(), true);

		return getBytesCRLF("220 OK");
	}

}
