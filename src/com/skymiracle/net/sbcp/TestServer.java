package com.skymiracle.net.sbcp;

import java.io.*;

import com.skymiracle.io.IOUtils;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.tcpServer.*;
import com.skymiracle.util.ByteUtils;

public class TestServer extends TcpServer {

	public static class TestHandler extends TcpConnHandler<TestServer> {

		@Override
		public void handleConnection() throws Exception {
			socket.setTcpNoDelay(true);

			final byte[] recvBuf = new byte[1024];
			while (true) {
				IOUtils.blockRead(socket.getInputStream(), recvBuf, 0, 4);
				final int dataLen = ByteUtils.bs2ushort(recvBuf, 2);
				IOUtils.blockRead(socket.getInputStream(), recvBuf, 4, dataLen);
				
				for (int i = 0; i < 1000; i++) {
					new Thread() {
						public void run() {
							try {
								for (int j = 0; j < 10; j++) {
						
									socket.getOutputStream().write(recvBuf,
												0, 4 + dataLen);
									
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		}

	}

	public TestServer() throws Exception {
		super("TestServer", 8001, TestHandler.class);
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws Exception {
		TestServer s = new TestServer();
		s.start();
	}
}
