package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.ArrayList;

import com.skymiracle.server.tcpServer.antiCracker.AntiCracker;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 向服务器标识用户身份
 */
public class AC_ListCommander extends SmtpAbsCommander{

	public AC_ListCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		AntiCracker ac = this.getSmtpServer().getAntiCracker();
		StringBuffer sb = new StringBuffer();
		
		ArrayList<String> banIps = ac.getBanIps();
		if(banIps != null){
			sb.append("+OK . " + banIps.size() +" IPs are banned. \r\n");
			for(String ip: banIps) {
				sb.append(ip).append("\r\n");
			}
		}else{
			sb.append("+OK . " + 0 +" IPs are banned. \r\n");
		}
		sb.append(".");
		return getBytesCRLF(sb.toString());
	}

}
