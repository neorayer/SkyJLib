package com.skymiracle.server.tcpServer.mailServer.testCase;

import java.util.HashMap;
import java.util.Map;

import com.skymiracle.server.tcpServer.mailServer.Smtp.AddrFilter;

public class WpxFilter implements AddrFilter{

	public Map<String, String> getFilterMap(String username, String domain)
			throws Exception {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		map.put("sky@test.com", "");
		map.put("xued@sina.com", "");
		return map;
	}

}
