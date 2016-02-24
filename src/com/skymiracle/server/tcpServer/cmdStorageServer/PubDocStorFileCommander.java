package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class PubDocStorFileCommander extends UserDomainCommander {

	public PubDocStorFileCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("storageName fname");

		String storageName = args[0];
		String fileName = args[1];

		println("220 OK");

		getPubDocAccessorLocal(storageName).storFile(
				getSocketInputStream(), fileName, true);

		return getBytesCRLF("220 OK");
	}

}
