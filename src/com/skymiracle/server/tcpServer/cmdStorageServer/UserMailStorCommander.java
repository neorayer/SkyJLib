package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailStorCommander extends UserDomainCommander {

	public UserMailStorCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("folder mail_UUID");

		String folderPathInBox = args[0];
		String mail_UUID = args[1];

		println("220 OK");

		getUsMailAccessorLocal(username, domain).mailStor(folderPathInBox,
				mail_UUID, getSocketInputStream(), true);

		return getBytesCRLF("220 OK");
	}

}
