package com.skymiracle.client.tcpClient;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.skymiracle.logger.Logger;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;

public class TcpClientAbs implements TcpClient {
	protected Socket socket;

	protected int port = 25;

	protected String host;

	protected InternetPrintWriter lnWriter;

	protected CRLFTerminatedReader lnReader;

	protected boolean connected = false;

	protected int timeout = 120000;

	public TcpClientAbs() {

	}

	public TcpClientAbs(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void connection() throws UnknownHostException, IOException {
		this.socket = new Socket(this.host, this.port);
		this.socket.setSoTimeout(this.timeout);
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
				this.socket.getOutputStream(), 1024), true);
		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
				"ASCII");
		this.connected = true;
		Logger.debug("Connected to " + this.host + ":" + this.port);
	}

	public void setTimeout(int timeoutMillsSeconds) {
		this.timeout = timeoutMillsSeconds;
	}

	public void disConnection() throws IOException {
		if (this.socket != null)
			this.socket.close();
		this.connected = false;
	}

	public void writeln(String string) throws IOException {
		this.lnWriter.println(string);
	}

	public String readln() throws UnsupportedEncodingException, IOException {
		return this.lnReader.readLine();
	}
}
