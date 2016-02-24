package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.auth.MailUser;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.MailServer;

public class PassCommander extends Pop3AbsCommander {

	public PassCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_USER)
			return null;

		String password = tail;

		MailUser mailUser = getPop3Server().authMail(phStatus.getUsername(),
				password, "POP3", getRemoteIP());
		
		if (mailUser == null) {
			phStatus.setStep(Pop3HandleStatus.STEP_INIT);
			((MailServer)getPop3Server()).debug("Auth Failed. Wrong password=" + password);
			return getBytesCRLF("-ERR Login failed.");
		}else {
			((MailServer)getPop3Server()).debug("Auth Succeed: " +  mailUser.toEmail());
		}

		if (!getPop3Server().hasNativePop3Permission(mailUser))
			return getBytesCRLF("-ERR Login failed.user has no pop3 permission");

		if (!mailUser.isOpen())
			return getBytesCRLF("-ERR Login failed.user status is "
					+ mailUser.getStatus());

		phStatus.setLogin(true);
		phStatus.setStep(Pop3HandleStatus.STEP_PASS);
		phStatus.setPassword(password);
		phStatus.setMailUser(mailUser);

		return getBytesCRLF("+OK Logged in");
	}

}
