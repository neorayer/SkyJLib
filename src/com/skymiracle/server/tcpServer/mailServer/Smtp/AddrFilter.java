package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.Map;

public interface AddrFilter {
	public Map<String,String> getFilterMap(String username,String domain) throws Exception;
}
