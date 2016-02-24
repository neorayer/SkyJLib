package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailRmClassCommander extends UserDomainCommander {

	public UserMailRmClassCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 1)
			return getHelpBytesCRLF("mailClassName");

		String mailClassName = args[0];

		getUsMailAccessorLocal(username, domain).mailRmMailClass(mailClassName);
		return getBytesCRLF("220 OK");
	}

}
