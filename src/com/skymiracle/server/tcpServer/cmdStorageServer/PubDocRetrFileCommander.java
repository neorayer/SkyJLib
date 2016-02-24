package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.io.File;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.io.StreamPipe;

public class PubDocRetrFileCommander extends UserDomainCommander {

	public PubDocRetrFileCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 2)
			return getHelpBytesCRLF("storageName filename");

		String storageName = args[0];
		String fileName = args[1];

		println("220 OK");
		readln();

		File file = getPubDocAccessorLocal(storageName).retrFile(
				fileName);
		StreamPipe.fileToOutput(file, getSocketOutputStream(), true);

		return getBytesCRLF("220 OK");
	}

}
