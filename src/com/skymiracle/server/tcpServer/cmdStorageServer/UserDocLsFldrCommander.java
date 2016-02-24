package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.util.List;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
/**
 *	用户网络硬盘文件夹查询命令 
 */
public class UserDocLsFldrCommander extends UserDomainCommander {

	public UserDocLsFldrCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 1)
			return getHelpBytesCRLF("folder isIncSub[true|false]");

		String folderPathInBox = args[0];
		boolean isIncSub = Boolean.parseBoolean(args[1]);
		List<String> folders = getUsDocAccessorLocal(username, domain).docLsFldr(
				folderPathInBox, isIncSub);

		println("220 OK count=" + folders.size());
		readln();

		StringBuffer sb = new StringBuffer();
		for (String folder : folders)
			sb.append(folder).append("\r\n");

		return getBytes(sb.toString());
	}

}
