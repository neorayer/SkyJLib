package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class PubDocDelFilesCommander extends UserDomainCommander {

	public PubDocDelFilesCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("storageName filename1|filename|filename|filename|");

		// filepaths
		String storageName = args[0];
		String[] fps = args[1].split("\\|");


	getPubDocAccessorLocal(storageName).deleteFile(
			fps);

		return getBytesCRLF("220 OK");
	}

}
