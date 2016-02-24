package com.skymiracle.server.tcpServer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.skymiracle.util.UUID;

/**
 * TCP连接处理线程
 */
public class TcpConnThread<T extends TcpServer> implements Runnable {
	private Socket socket;

	private TcpConnHandler<T> connHandler;

	private T tcpServer;

	/**
	 * constructer init the socket,connhandler and tcpServer for managing the
	 * coming client socket
	 * 
	 * @param tcpServer
	 * @param socket
	 * @param connHandler
	 */
	public TcpConnThread(T tcpServer, Socket socket,
			TcpConnHandler<T> connHandler) {
		this.tcpServer = tcpServer;
		this.socket = socket;
		try {
			this.socket
					.setSoTimeout(this.tcpServer.getCmdTimeoutSeconds() * 1000);
		} catch (SocketException e) {
			this.tcpServer.error(new StringBuffer(this.tcpServer.getName())
					.append(" TcpConnThread").toString(), e);
		}
		this.connHandler = connHandler;
		// setName(this.tcpServer.getName() + " connection Thread");
	}

	public void run() {
		// modify conn count
		this.tcpServer.modConnCount(1);

		// record current connection information
		String remoteIP = this.socket.getInetAddress().getHostAddress();
		String connID = new UUID().toShortString();
		this.tcpServer.addCurTcpConn(connID, remoteIP);

		// handle connection
		try {
			this.connHandler.handleConnection(this.tcpServer, this.socket);
		} catch (SocketTimeoutException etimeout) {
			this.tcpServer.warn(new StringBuffer(this.tcpServer.getName()).append(
					": Socket Timeout ").append(remoteIP).toString());
		} catch (Exception e) {
			this.tcpServer.warn("TcpConnThread.run", e);
		}

		// close socket
		try {
			if (this.tcpServer.isCloseSocketAfterHandle()) {
				this.connHandler.closeSocketAfterHandle();
				this.tcpServer.info("Disconnected.");
			}
		} catch (IOException e) {
			this.tcpServer.warn(new StringBuffer(this.tcpServer.getName()).append(
					": Socket close exception ").append(remoteIP).toString());
		}

		// modify connection count
		this.tcpServer.modConnCount(-1);

		// modify connection information
		this.tcpServer.delCurTcpConn(connID, remoteIP);
	}
}
