package com.skymiracle.server.tcpServer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;
import com.skymiracle.util.UUID;

/**
 * TCP服务连接处理器
 */
public abstract class TcpConnHandler<T extends TcpServer> {

	protected T tcpServer;

	protected Socket socket;

	private String remoteIP;

	private int remotePort;

	protected InputStream in;

	protected CRLFTerminatedReader lnReader;

	protected OutputStream out;

	protected InternetPrintWriter lnWriter;

	protected StringBuffer connLogHeadSb;

	protected String connUUID;

	protected Object handleStatus;

	/**
	 * manage the client socket and complete the handle of the server
	 * 
	 * @param thread
	 * @param socket
	 * @throws Exception
	 */
	public void handleConnection(T tcpServer, Socket socket) throws Exception {
		this.tcpServer = tcpServer;
		this.socket = socket;
		this.in = this.socket.getInputStream();
		this.lnReader = new CRLFTerminatedReader(this.in, this.tcpServer
				.getDefaultCharset());
		this.out = this.socket.getOutputStream();
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(this.out,
				1024), true);
		this.remoteIP = this.socket.getInetAddress().getHostAddress();
		this.remotePort = this.socket.getPort();

		this.connUUID = new UUID().toShortString();

		// log head StringBuffer
		this.connLogHeadSb = new StringBuffer(this.tcpServer.getName()).append(
				":").append(" [").append(this.remoteIP).append("]");

		// check IP reject
		if (this.tcpServer.isRejectIP(this.remoteIP)) {
			lnWriter.println("554 IP reject by host."
					+ this.tcpServer.getRejectIPReason(this.remoteIP));
			this.tcpServer.info("Reject Connection.");
			return;
		}

		// check max connection
		if (this.tcpServer.isMoreThanMaxConn()) {
			lnWriter.println("554 Connection reject. Too many connection.");
			String _s = "refused connection - More than maxConn("
					+ this.tcpServer.getMaxConn() + ")";
			this.tcpServer.info(_s);
			return;
		}

		// check max ip cur connection
		if (this.tcpServer.isMoreThanMaxIpCurConn(this.remoteIP)) {
			lnWriter
					.println("554 Connection reject. Too many connection from same ip.");
			String _s = "refused connection - More than maxIPCurConn("
					+ this.tcpServer.getMaxIPCurConn() + ")";
			this.tcpServer.info(_s);
			return;
		}

		this.tcpServer.info(this.remoteIP + ":" + this.remotePort
				+ " Connected.");

		// real handle connection
		boolean isFilterPass = true;
		for (ConnectionFilter filter : this.tcpServer.getConnectionFilters()) {
			if (filter.doFilter(this))
				continue;
			else {
				isFilterPass = false;
				break;
			}
		}

		if (isFilterPass)
			handleConnection();
		this.tcpServer.getConnClosedFilter().doFilter(this);
	}

	
	
	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}

	public CRLFTerminatedReader getLnReader() {
		return lnReader;
	}

	public InternetPrintWriter getLnWriter() {
		return lnWriter;
	}

	public String getRemoteIP() {
		return this.remoteIP;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public Object getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(Object handleStatus) {
		this.handleStatus = handleStatus;
	}

	public void closeSocketAfterHandle() throws IOException {
		// TODO not safe
		if (this.lnReader != null)
			try {
				this.lnReader.close();
			} catch (IOException e) {
				// 这里不能throw exception
				throw e;
			}

		if (this.lnWriter != null)
			this.lnWriter.close();

		if (this.socket != null)
			try {
				this.socket.close();
			} catch (IOException e) {
				throw e;
			}
	}

	public abstract void handleConnection() throws Exception;

}
