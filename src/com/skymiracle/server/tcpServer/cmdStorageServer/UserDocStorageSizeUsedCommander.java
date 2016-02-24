package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 文件空间查询命令
 */
public class UserDocStorageSizeUsedCommander extends UserDomainCommander {

	public UserDocStorageSizeUsedCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		long size = getUsDocAccessorLocal(username, domain)
				.docGetStorageSizeUsed();
		return getBytesCRLF("" + size);
	}

}
