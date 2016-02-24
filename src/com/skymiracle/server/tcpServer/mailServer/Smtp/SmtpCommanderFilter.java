package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public interface SmtpCommanderFilter {

	public String doFilter(CmdConnHandler connHandler, SmtpHandleStatus smtpHandleStatus);
}
