package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.List;

import com.skymiracle.auth.MailUser;

public interface SmtpLogger {

	void addFromNativeMission(String missionUUID, MailUser fromMailUser,
			List<MailUser> rcptToList, long dataSize, List<String> dataLineList);

}
