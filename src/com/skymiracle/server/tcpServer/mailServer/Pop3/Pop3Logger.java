package com.skymiracle.server.tcpServer.mailServer.Pop3;

public interface Pop3Logger {

	void retrBegin(String mailUuid, String username, long size, String remoteIP);

}
