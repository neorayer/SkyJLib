package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.fileBox.MailClass;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserMailAddClassCommander extends UserDomainCommander {

	public UserMailAddClassCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] doCmd(String username, String domain, String tail)
			throws Exception {
		String[] args = tail.split(" ");
		if (args.length < 5)
			return getHelpBytesCRLF("mailClassName mailClassFolderName target op keyWord ");

		String mailClassName = args[0];
		String mailClassFolderName = args[1];
		String target = args[2];
		String op = args[3];
		String keyWord = args[4];
		MailClass mailClass = new MailClass(mailClassName, mailClassFolderName,
				target, op, keyWord);

		getUsMailAccessorLocal(username, domain).mailAddClass(mailClass);
		return getBytesCRLF("220 OK");
	}

}
