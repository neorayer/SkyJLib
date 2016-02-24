package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 邮件空间查询命令
 */
public class UserMailStorageSizeUsedCommander extends UserDomainCommander {

	public UserMailStorageSizeUsedCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		long size = getUsMailAccessorLocal(username, domain)
				.mailGetStorageSizeUsed();
		return getBytesCRLF("" + size);
	}

}
