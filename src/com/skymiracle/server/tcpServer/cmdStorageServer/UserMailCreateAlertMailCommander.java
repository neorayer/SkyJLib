package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailCreateAlertMailCommander extends UserDomainCommander {

	public UserMailCreateAlertMailCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("mailSize spaceAlert");

		long mailSize = Long.parseLong(args[0]);
		long spaceAlert = Long.parseLong(args[1]);

		getUsMailAccessorLocal(username, domain).mailCreateAlertMail(mailSize,
				spaceAlert);
		return getBytesCRLF("220 OK");
	}

}
