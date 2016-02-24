package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.auth.MailUser;
import com.skymiracle.server.tcpServer.mailServer.MailServer;

public class Pop3Server extends MailServer {

	private Pop3Logger pop3Logger = new Pop3Logger() {

		public void retrBegin(String mailUuid, String username, long size,
				String remoteIP) {
		}

	};

	public Pop3Server() throws Exception {
		super("Pop3Server", 110);

		setWelcome("+OK WELCOME to SkyMiracle POP3 Server.");
		setUnknown("-ERR Invalid command.");
		setShortConn(false);

		//Commands for standard pop3 protocol
		addCommander(UserCommander.class);
		addCommander(PassCommander.class);
		addCommander(NoopCommander.class);
		addCommander(ListCommander.class);
		addCommander(DeleCommander.class);
		addCommander(RetrCommander.class);
		addCommander(RsetCommander.class);
		addCommander(StatCommander.class);
		addCommander(TopCommander.class);
		addCommander(UidlCommander.class);
		addCommander(QuitCommander.class);
	
		//Commands for AntiCracker management
		addCommander(AC_ListCommander.class);
		addCommander(AC_DeleCommander.class);

	}

	public boolean hasNativePop3Permission(MailUser mailUser) {
		return getAuthMail().hasPermissionPop3(mailUser);
	}

	public Pop3Logger getPop3Logger() {
		return pop3Logger;
	}

	public void setPop3Logger(Pop3Logger pop3Logger) {
		this.pop3Logger = pop3Logger;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skymiracle.logger.Logger1#debug(java.lang.String)
	 */
	public void debug(String s) {
		if (s != null && s.toUpperCase().indexOf("PASS") > 0) {
			s = "PASS ******";
		}
		super.debug(s);
	}

}
