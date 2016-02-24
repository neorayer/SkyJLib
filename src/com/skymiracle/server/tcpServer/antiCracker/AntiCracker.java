package com.skymiracle.server.tcpServer.antiCracker;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.server.tcpServer.ConnectionFilter;

public interface AntiCracker extends ConnectionFilter{

	public void doFilterAfterAuth(boolean isSucc, String id, String modeName, String remoteIP);

	public ArrayList<String> getBanIps();

	public boolean isAdminSecurityKeyCheck(String inPass);

	public boolean removeBan(String ip);
}
