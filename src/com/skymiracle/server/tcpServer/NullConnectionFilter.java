package com.skymiracle.server.tcpServer;

public class NullConnectionFilter implements ConnectionFilter {

	public boolean doFilter(TcpConnHandler connHandler) {
		return true;
	}

}
