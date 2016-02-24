package com.skymiracle.server.udpServer.testCase;

import java.net.DatagramPacket;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.udpServer.Commander;
import com.skymiracle.server.udpServer.UdpCmdHandler;

public class EchoCommander extends Commander {


	public EchoCommander(UdpCmdHandler cmdHandler) {
		super(cmdHandler);
	}

	@Override
	public byte[] doCmd(String[] args, DatagramPacket dPacket) {
		String arg = args[1];
		return arg.getBytes();
	}

}
