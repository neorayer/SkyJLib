package com.skymiracle.server.udpServer.testCase;

import com.skymiracle.server.udpServer.*;

public class EchoUdpServer extends UdpCmdServer {

	public EchoUdpServer() throws Exception {
		super("EchoUdpServer", 9898);

		addCommander(EchoCommander.class);
	}

	public static void main(String[] args) {
		try {
			EchoUdpServer eus = new EchoUdpServer();
			eus.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
