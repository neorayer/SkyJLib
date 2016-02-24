package com.skymiracle.server.tcpServer.antiCracker;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.server.tcpServer.TcpConnHandler;
import com.skymiracle.server.tcpServer.TcpServer;

public class NoneAntiCracker implements AntiCracker {

	public void doFilterAfterAuth(boolean isSucc, String id, String modeName,
			String remoteIP) {
		// TODO Auto-generated method stub

	}

	public boolean doFilter(TcpConnHandler<? extends TcpServer> connHandler) {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<String> getBanIps() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAdminSecurityKeyCheck(String inPass) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeBan(String ip) {
		return true;
		// TODO Auto-generated method stub
		
	}

}
