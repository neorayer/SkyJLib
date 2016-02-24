package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailGetClassesCommander extends UserDomainCommander {

	public UserMailGetClassesCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {

		byte[] bytes = getUsMailAccessorLocal(username, domain)
				.mailGetMailClassesBytes();

		println("220 OK");
		readln();

		return bytes;
	}

}
