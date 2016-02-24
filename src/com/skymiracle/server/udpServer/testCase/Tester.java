package com.skymiracle.server.udpServer.testCase;

import java.net.DatagramPacket;

import com.skymiracle.client.udpClient.UdpClient;

import junit.framework.TestCase;

public class Tester extends TestCase {

	public void testFoo() throws Exception {
		//启动服务器
		EchoUdpServer srv = new EchoUdpServer();
		srv.setPort(10000);
		srv.start();

		UdpClient client1 = new UdpClient("127.0.0.1", 10000);

		
		UdpClient client2 = new UdpClient("127.0.0.1", 10000);
		for (int i=0; i<100; i++) {
			client1.sendCmd("Echo " + ( i + 10));
			String resp2 = client2.oneCmd("Echo " + i);
			assertEquals(i + "", resp2);
			String resp1 = client1.readString();
			assertEquals((i + 10) + "", resp1);
		}

		srv.stop();
	}
}
