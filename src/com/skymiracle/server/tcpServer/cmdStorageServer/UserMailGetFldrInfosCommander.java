package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.util.List;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.MailFolderInfo;
import com.skymiracle.xml.*;

/**
 *	取得所有邮件文件夹命令
 */
public class UserMailGetFldrInfosCommander extends UserDomainCommander {

	public UserMailGetFldrInfosCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		List<MailFolderInfo> infos = getUsMailAccessorLocal(username, domain)
				.mailGetFldrInfos();

		println("220 OK count=" + infos.size());
		readln();

		String xml = XmlTools.getXml(MailFolderInfo.class.getSimpleName(), infos);
		return getBytes(xml);
	}

}
