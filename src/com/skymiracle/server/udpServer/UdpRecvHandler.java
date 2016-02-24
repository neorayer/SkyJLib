package com.skymiracle.server.udpServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class UdpRecvHandler {

	protected UdpServer udpServer;

	protected DatagramPacket dPacket;

	protected DatagramSocket dSocket;

	public void handleDataRecv(UdpServer udpServer, DatagramPacket dPacket)
			throws Exception {
		this.udpServer = udpServer;
		this.dPacket = dPacket;
		dSocket = udpServer.getDSocket();

		handleDataRecv();
	}

	public abstract void handleDataRecv() throws Exception;

}
