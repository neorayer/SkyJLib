package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.cmdServer.Commander;

public abstract class SmtpAbsCommander extends Commander {

	protected SmtpHandleStatus shStatus;

	public SmtpAbsCommander(CmdConnHandler connHandler) {
		super(connHandler);
		this.shStatus = getHandleStatus();
	}

	protected SmtpServer getSmtpServer() {
		return (SmtpServer) getCmdServer();
	}

	private SmtpHandleStatus getHandleStatus() {
		SmtpHandleStatus hs = (SmtpHandleStatus) this.connHandler
				.getHandleStatus();
		if (hs == null) {
			hs = new SmtpHandleStatus(getSmtpServer());
		}
		this.connHandler.setHandleStatus(hs);
		return hs;
	}

	protected String returnCode(int code) {
		return returnCode(code, "");
	}

	protected String returnCode(int code, String additionMsg) {
		String msg = "";

		switch (code) {
		case 235:
			msg = "Authentication successful.";
			break;
		case 250:
			msg = "OK";
			break;
		case 354:
			msg = "End data with <CR><LF>.<CR><LF>";
			break;
		case 432:
			msg = "A password transition is needed";
			break;
		case 534:
			msg = "Authentication mechanism is too weak";
			break;
		case 535:
			msg = "Authentication Failed";
			break;
		case 538:
			msg = "538 Encryption required for requested authentication mechanism";
			break;
		case 454:
			msg = "454 Temporary authentication failure";
			break;
		case 530:
			msg = "530 Authentication required";
			break;
		case 503:
			msg = "Bad sequence of commands.";
			break;
		case 504:
			msg = "Unrecognized authentication type.";
			break;
		case 550:
			msg = "Requested action not taken:";
		case 553:
			msg = "Requested action not taken: mailbox name not allowed. [E.g., mailbox syntax incorrect].";
			break;
		case 554:
			msg = "Transaction failed.";
			break;
		default:
			msg = "";
		}
		return code + " " + msg + " " + additionMsg;
	}

}
