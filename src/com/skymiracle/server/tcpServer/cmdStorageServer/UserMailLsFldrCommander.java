package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.util.List;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailLsFldrCommander extends UserDomainCommander {

	public UserMailLsFldrCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		List<String>folders = getUsMailAccessorLocal(username, domain)
				.mailLsFldr();

		println("220 OK count=" + folders.size());
		readln();

		StringBuffer sb = new StringBuffer();
		for (String folder : folders)
			sb.append(folder).append("\r\n");

		return getBytes(sb.toString());
	}

}
