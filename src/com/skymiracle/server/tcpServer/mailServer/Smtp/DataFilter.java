package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.List;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public interface DataFilter {

	String doFilter(CmdConnHandler connHandler, SmtpHandleStatus shStatus,
			List<String> dataLineList);

}
