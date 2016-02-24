package com.skymiracle.server.tcpServer.mailServer.Smtp.filters;

import javax.naming.NamingException;

import com.skymiracle.dns.Dns;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpCommanderFilter;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpHandleStatus;
import com.skymiracle.logger.Logger;

public class SourceIpSegmentFilter implements SmtpCommanderFilter {

	private int level = 1;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String doFilter(CmdConnHandler connHandler,
			SmtpHandleStatus smtpHandleStatus) {

		String domain = smtpHandleStatus.getFromMailUser().getDc();
		String ip = connHandler.getRemoteIP();
		String heloName = smtpHandleStatus.getHeloName();

		if (isRejectMailFrom(domain, ip, level))
			return "554 mail from DNS not match:" + ip + ":" + domain;

		try {
			if (!Dns.likeAOrMxRecord(heloName, ip, level))
				return "554 heloName DNS not match:" + ip + ":" + heloName;
			else
				return null;
		} catch (NamingException e) {
			return null;
		}
	}

	public boolean isRejectMailFrom(String domain, String ip, int level) {
		try {
			return !(Dns.likeAOrMxRecord(domain, ip, level));
		} catch (NamingException e) {
			Logger.debug("Dns resolv failed: " + domain);
			return true;
		}
	}

	public boolean isRejectHelo(String domain, String ip, int level) {
		try {
			return !(Dns.likeAOrMxRecord(domain, ip, level));
		} catch (NamingException e) {
			Logger.debug("Dns resolv failed: " + domain);
			return false;
		}
	}

}
