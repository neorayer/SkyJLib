package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.util.List;

import com.skymiracle.fileBox.FileBoxLsItem;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 *	文件列表查找命令 
 */
public class UserDocLsFileCommander extends UserDomainCommander {

	public UserDocLsFileCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String folderPathInBox = tail.trim();
		if (folderPathInBox.length() < 1)
			return getHelpBytesCRLF("folder");

		println("220 OK");
		readln();

		List<FileBoxLsItem> items = getUsDocAccessorLocal(username, domain)
				.docLsFile(folderPathInBox);
		StringBuffer sb = new StringBuffer();
		for (FileBoxLsItem item: items) {
			sb.append(item.getFileName()).append("\t");
			sb.append(item.getSize()).append("\t");
			sb.append(item.getLastModified()).append("\t");
			sb.append("\r\n");
		}

		return getBytes(sb.toString());
	}

}
