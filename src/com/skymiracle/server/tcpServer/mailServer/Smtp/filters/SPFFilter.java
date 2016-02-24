package com.skymiracle.server.tcpServer.mailServer.Smtp.filters;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpCommanderFilter;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpHandleStatus;
import com.skymiracle.spf.SPFChecker;
import com.skymiracle.logger.Logger;

public class SPFFilter implements SmtpCommanderFilter {

	private SPFChecker spfChecker;

	public String doFilter(CmdConnHandler connHandler,
			SmtpHandleStatus smtpHandleStatus) {
		String ip = connHandler.getRemoteIP();
		String domain = smtpHandleStatus.getFromMailUser().getDc();

		int res = spfChecker.doCheck(ip, domain);

		switch (res) {
		case SPFChecker.RESULT_NONE:
			return null;
		case SPFChecker.RESULT_SUCC:
			return null;
		case SPFChecker.RESULT_FAIL:
			{
				Logger.info("554 The remote IP does not pass SPF checking! " + ip +" " + domain);
				return "554 The remote IP does not pass SPF checking! " + ip +" " + domain;
			}
		default:
			return null;
		}
	}

	public SPFChecker getSpfChecker() {
		return spfChecker;
	}

	public void setSpfChecker(SPFChecker spfChecker) {
		this.spfChecker = spfChecker;
	}

}
