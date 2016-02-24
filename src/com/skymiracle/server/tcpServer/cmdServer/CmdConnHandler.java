package com.skymiracle.server.tcpServer.cmdServer;

import java.net.Socket;

import com.skymiracle.server.tcpServer.TcpConnHandler;

public class CmdConnHandler extends TcpConnHandler<CmdServer> {
	protected String s, sHead, sTail;

	private boolean quiting = false;
	
	public String getCharset() {
		return this.tcpServer.getDefaultCharset();
	}

	public CmdServer getCmdServer() {
		return this.tcpServer;
	}

	@Override
	public void handleConnection() throws Exception {
		String welcome = this.tcpServer.getWelcome();
		if (welcome != null)
			this.lnWriter.println(welcome);

		while (true) {
			if (this.socket.isClosed())
				break;
			this.s = this.lnReader.readLine();
			
			if(this.s == null)
				break;
			
			this.tcpServer.debug("<- " + this.s);
			this.sHead = this.s.toLowerCase();
			this.sTail = "";
			int pos = this.s.indexOf(' ');
			if (pos > 0) {
				this.sHead = this.s.substring(0, pos).toUpperCase();
				this.sTail = this.s.substring(pos + 1).trim();
			}
			
			// check quit
			if (this.tcpServer.isQuitCmd(this.sHead))
				break;

			// doCmd
			byte[] bs = this.tcpServer.doCmd(this, this.sHead, this.sTail);

			// output bytes
			if (bs == null) {
				String unknown = this.tcpServer.getUnknown();
				this.lnWriter.println(unknown);
				this.lnWriter.flush();
				this.tcpServer.detail("-> " + unknown);
			} else {
				if (this.socket.isClosed())
					break;
				this.socket.getOutputStream().write(bs);
				this.socket.getOutputStream().flush();
				this.tcpServer.detail("-> " + new String(bs));
			}
			if (this.quiting)
				break;
			
			if (this.tcpServer.isShortConn())
				break;
		}
//		this.lnReader.close();
//		this.lnWriter.close();
//		this.socket.close();
	}

	public Socket getSocket() {
		return this.socket;
	}

	public void setQuiting(boolean quiting) {
		this.quiting = quiting;
	}

}
