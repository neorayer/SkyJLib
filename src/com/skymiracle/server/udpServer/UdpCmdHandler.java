package com.skymiracle.server.udpServer;

import java.io.IOException;
import com.skymiracle.logger.Logger;

public class UdpCmdHandler extends UdpRecvHandler {

	private UdpCmdServer udpCmdServer;

	@Override
	public void handleDataRecv() throws Exception {
		this.udpCmdServer = (UdpCmdServer) this.udpServer;
		final UdpCmdServer udpCmdServer = this.udpCmdServer;
		final int bufLen = this.dPacket.getLength();
		final byte[] buf = this.dPacket.getData();

		String s = new String(buf, 0, bufLen, this.udpCmdServer.getCharset());
		Logger.detail("Udp Server: <- " + s);

		int firstLineIdx = s.indexOf("\r\n");
		String firstLine = s;
		if (firstLineIdx >= 0)
			firstLine = s.substring(0, firstLineIdx).trim();
		final String[] args = firstLine.split(" ");
		final String cmd = args[0];
		//final int dataOffset = firstLineIdx + 2;
		final UdpCmdHandler udpCmdHandler = this;

		new Thread() {
			@Override
			public void run() {
				try {
					byte[] resBs = udpCmdServer.doCmd(udpCmdHandler, cmd, args,	dPacket);
					if (resBs != null)
						udpCmdHandler.send(resBs);
					
				} catch (Exception e) {
					Logger.error("", e);
				}
			}
		}.start();
	}

	protected void send(byte[] resBs) throws IOException {
		this.dPacket.setData(resBs);
		this.dPacket.setLength(resBs.length);
		this.dSocket.send(this.dPacket);

	}

	public UdpCmdServer getUdpCmdServer() {
		return this.udpCmdServer;
	}

	public String getCharset() {
		return this.udpCmdServer.getCharset();
	}

}
