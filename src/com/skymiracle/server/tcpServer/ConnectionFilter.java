package com.skymiracle.server.tcpServer;
/**
 * 连接过滤器
 */
public interface ConnectionFilter {

	/**
	 * 
	 * @param connHandler
	 * @return true - if filter pass, false if not.
	 */
	boolean doFilter(TcpConnHandler<? extends TcpServer> connHandler);

}
