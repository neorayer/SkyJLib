package com.skymiracle.server.tcpServer.cmdServer.testCase;

import com.skymiracle.server.tcpServer.cmdServer.CmdServer;

public class TestServer extends CmdServer {

	public TestServer(String name, int port) throws Exception {
		super(name, port);
		addCommander(EchoCommander.class);
	}

	public static void main(String[] args) throws Exception {
		TestServer server = new TestServer("TestServer", 9900);
		server.start();
	}
}
