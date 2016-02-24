package com.skymiracle.client.udpClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import com.skymiracle.logger.Logger;

public class UdpClient {

	private final static int MAX_READBUF_LEN = 4096;

	protected String host = "127.0.0.1";

	protected int port;

	private int timeoutMills = 5000;

	protected DatagramSocket dSocket;

	private String charset = "UTF-8";

	//TODO: 注意：这里的localPort根本没被使用
	private int localPort = 0;

	public UdpClient() throws SocketException {
		this.dSocket = new DatagramSocket();
		this.dSocket.setSoTimeout(timeoutMills);
	}

	public UdpClient(int localPort) throws SocketException {
		this.localPort = localPort;
		this.dSocket = new DatagramSocket(localPort);
		this.dSocket.setSoTimeout(timeoutMills);
	}

	public UdpClient(String host, int port) throws SocketException {
		this();
		this.host = host;
		this.port = port;
	}

	public UdpClient(int localPort, String host, int port)
			throws SocketException {
		this(localPort);
		this.host = host;
		this.port = port;
	}

	public int getLocalPort() {
		return this.localPort;
	}
	public void close() {
		this.dSocket.close();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getTimeoutMills() {
		return timeoutMills;
	}

	public void setTimeoutMills(int timeoutMills) {
		this.timeoutMills = timeoutMills;
	}

	public DatagramPacket sendCmd(String cmd) throws IOException {
		byte[] bs = cmd.getBytes(charset);
		return sendBytes(bs);
	}

	public String oneCmd(String cmd) throws IOException {
		DatagramPacket pac = sendCmd(cmd);
		Logger.detail("Udp Client: -> " + this.host + ":" + this.port + " len="
				+ cmd.length());

		byte[] readBuf = new byte[MAX_READBUF_LEN];
		pac.setData(readBuf);
		pac.setLength(readBuf.length);

		return readString();
//		dSocket.receive(pac);
//		int rLen = pac.getLength();
//		if (rLen < 0)
//			return null;
//		if (rLen == 0)
//			return "";
//		String res = new String(readBuf, 0, rLen, charset);
//		Logger.detail("Udp Client: <- " + res);
//		return res;
	}

	public int oneCmd(String cmd, byte[] buf) throws IOException {
		DatagramPacket pac = sendCmd(cmd);
		pac.setData(buf);
		pac.setLength(buf.length);
		dSocket.receive(pac);
		return pac.getLength();
	}

	public DatagramPacket sendBytes(byte[] bs) throws IOException {
		DatagramPacket pac = new DatagramPacket(bs, bs.length, InetAddress
				.getByName(host), port);
		this.dSocket.send(pac);
		return pac;
	}

	public DatagramPacket recv(byte[] bs) throws IOException {
		DatagramPacket pac = new DatagramPacket(bs, bs.length, InetAddress
				.getByName(host), port);
		dSocket.receive(pac);
		return pac;
	}
	
	public String readString() throws IOException {
		byte[] recvBuf = new byte[MAX_READBUF_LEN];
		try {
			DatagramPacket dp = recv(recvBuf);
			String s = new String(dp.getData(), 0, dp.getLength());
			Logger.detail("Udp Client: <- " + s);
			return s;
		}catch(SocketException e) {
			if (dSocket.isClosed())
				return null;
			throw e;
		}
	}



}
