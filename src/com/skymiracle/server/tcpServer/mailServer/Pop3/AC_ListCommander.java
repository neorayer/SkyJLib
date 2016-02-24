package com.skymiracle.server.tcpServer.mailServer.Pop3;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.server.tcpServer.antiCracker.AntiCracker;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3AbsCommander.Pop3HandleStatus.Mail;

/**
 * 输出AntiCracker的Ban IP列表
 *
 */
public class AC_ListCommander extends Pop3AbsCommander {

	public AC_ListCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		AntiCracker ac = this.getPop3Server().getAntiCracker();
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
		return this.getBytesCRLF(sb.toString());
	}

}
