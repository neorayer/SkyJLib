package com.skymiracle.server.tcpServer;

public interface Issue {
	public boolean webIssue(String domain, String issueDest);

	public boolean confIssue();
}
