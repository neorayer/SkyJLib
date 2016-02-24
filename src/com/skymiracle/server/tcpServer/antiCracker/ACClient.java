package com.skymiracle.server.tcpServer.antiCracker;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.logger.Logger;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;

/**
 * 存储远程访问接口 邮件“界面系统” 可能与 “文件存储系统”不在一台服务器上。所以需要通过TCP协议远程访问
 */
public class ACClient {

	// 安全码
	protected String adminSecurityKey = "111111";

	// 服务器IP
	protected String host = "127.0.0.1";

	// 服务器端口
	protected int port = 110;

	// 访问套接字
	protected Socket socket;

	// 输出流
	protected InternetPrintWriter lnWriter;

	// 输入流
	protected CRLFTerminatedReader lnReader;

	public ACClient() {
	}

	public String getAdminSecurityKey() {
		return adminSecurityKey;
	}

	public void setAdminSecurityKey(String adminSecurityKey) {
		this.adminSecurityKey = adminSecurityKey;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private void openSocket() throws UnknownHostException, IOException {
		Logger.debug("ACClient: openSocket: host:" + this.host + ", port:"
				+ port);
		this.socket = new Socket(this.host, this.port);
		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
				"ISO8859-1");
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
				this.socket.getOutputStream(), 1024), true);

		this.lnReader.readLine();
	}

	private void closeSocket() throws IOException {
		Logger.debug("ACClient: closeSocket");
		try {
			if (this.lnReader != null)
				this.lnReader.close();
		} catch (Exception e) {
			this.lnReader = null;
		}
		try {
			if (this.lnWriter != null)
				this.lnWriter.close();
		} catch (Exception e) {
			this.lnWriter = null;
		}
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (Exception e) {
			this.socket = null;
		}
	}

	private void println(String s) {
		this.lnWriter.println(s);
		Logger.detail("ACClient: -> " + s);
	}

	private String readln() throws IOException {
		String s = this.lnReader.readLine();
		Logger.detail("ACClient: <- " + s);
		return s;
	}

	public List<String> getBannedIPs() throws IOException {
		openSocket();
		List<String> ips = new LinkedList<String>();
		println("ac_list");
		readln();

		String s = "";
		for (;;) {
			s = readln();
			if (s.equals("."))
				break;

			ips.add(s);
		}
		closeSocket();
		return ips;
	}

	public void deleteIPs(String[] ips) throws IOException {
		openSocket();
		for (String ip : ips) {
			println("ac_dele " + this.adminSecurityKey + " " + ip);
			readln();
		}
		closeSocket();
	}

}
