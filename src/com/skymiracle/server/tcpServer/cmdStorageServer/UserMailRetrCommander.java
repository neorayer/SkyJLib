package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.io.StreamPipe;

public class UserMailRetrCommander extends UserDomainCommander {

	public UserMailRetrCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("folder uuid");

		String folderPathInBox = args[0];
		String uuid = args[1];

		println("220 OK");
		readln();

		String fpath = getUsMailAccessorLocal(username, domain).mailRetr(
				folderPathInBox, uuid);
		StreamPipe.fileToOutput(fpath, getSocketOutputStream(), true);

		return getBytesCRLF("220 OK");
	}

}
