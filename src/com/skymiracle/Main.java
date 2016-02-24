package com.skymiracle;

import java.net.InetAddress;

import com.skymiracle.client.tcpClient.smtpClient.SmtpSendException;
import com.skymiracle.client.tcpClient.smtpClient.SmtpSendResult.ResultItem;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(InetAddress.getByName("localhost").getHostAddress());
		String smtpHost = "makescn.com";
		if(!"127.0.0.1".equals(smtpHost)) { 
			String ip = InetAddress.getByName(smtpHost).getHostAddress();
			if ( "127.0.0.1".equals(ip) || "0.0.0.0".equals(ip)) 
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, "doamin[" + smtpHost + "]" + " is not exsits");
		};
	}

}
