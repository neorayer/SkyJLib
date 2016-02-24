package com.skymiracle.server.tcpServer.authImgServer;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.TcpConnHandler;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;


public class AuthImgServerConnHandler extends TcpConnHandler<AuthImgServer> {
	private static String MSG_WELCOME = "220 WELCOME to SkyMiracle Auth Img Server.";

	private final static int CMDTYPE_UNKNOWN = 0;

	private final static int CMDTYPE_GET = 10;

	private String sTail = null;

	private InternetPrintWriter lnWriter = null;

	private int cmdType = CMDTYPE_UNKNOWN;

	@Override
	public void handleConnection() throws Exception {
//		CRLFTerminatedReader lnReader = new CRLFTerminatedReader(this.socket
//				.getInputStream(), "ASCII");
//
//		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
//				this.socket.getOutputStream(), 1024), true);

		this.lnWriter.println(MSG_WELCOME);

		try {
			String s = lnReader.readLine();
			if (s == null)
				throw new Exception("socket read nothing.");
			Logger.debug("C: " + s);
			String sHead = s.toLowerCase();
			StringBuffer sb;

			int pos = s.indexOf(' ');
			if (pos > 0) {
				sHead = s.substring(0, pos).toLowerCase();
				this.sTail = s.substring(pos + 1);
			} else
				this.sTail = "";

			this.cmdType = getCmdType(sHead);
			switch (this.cmdType) {
			case CMDTYPE_GET:
				sb = doCmd_GET();
				break;
			default:
				sb = new StringBuffer("500 -ERR Invalid command");
				break;
			}
			Logger.debug("S: " + sb.toString());
			this.lnWriter.println(sb.toString());
		} catch (Exception e) {
			Logger.warn(new StringBuffer(this.tcpServer.getName())
					.append(": handleConnection exception."), e);
		}
	}

	private StringBuffer doCmd_GET() throws IOException {
		String v;
		if (this.sTail.length() == 0)
			v = "UNKNOWN";
		else
			v = this.sTail.trim();

		String path = this.tcpServer.createAuthImage(v);
		return new StringBuffer("OK " + path);
	}

	private int getCmdType(String sHead) {
		if (sHead.equalsIgnoreCase("get"))
			return CMDTYPE_GET;

		return CMDTYPE_UNKNOWN;
	}
}
