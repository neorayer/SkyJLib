package com.skymiracle.client.tcpClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skymiracle.logger.SimpleAppLogger;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;

public class AbsSocketClient extends SimpleAppLogger {

	private String host = "127.0.0.1";

	private int port = 9000;

	protected Socket socket;

	private InternetPrintWriter lnWriter;

	private boolean isCRLF = true;

	private BufferedReader lnReader;

	private int timeoutSeconds = 0;

	private int localPort = 0;

	protected InputStream inputStream;

	protected OutputStream outputStream;

	public AbsSocketClient() {
		super();
	}

	public AbsSocketClient(boolean isCRLF) {
		super();
		this.isCRLF = isCRLF;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	protected void openSocket() throws UnknownHostException, IOException {
		debug("openSocket... " + this.host + ":" + this.port);
		if (this.localPort > 0) {
			this.socket = new Socket(this.host, this.port, null, this.localPort);
		} else {
			this.socket = new Socket(this.host, this.port);
		}
		// this.socket = new Socket();
		// this.socket.setReuseAddress(true);
		// if (this.localPort > 0)
		// this.socket.bind(new InetSocketAddress(this.localPort));
		// this.socket.connect(new InetSocketAddress(this.host, this.port));
		this.socket.setSoTimeout(this.timeoutSeconds * 1000);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		this.lnReader = this.isCRLF ? new CRLFTerminatedReader(inputStream,
				"UTF-8") : new BufferedReader(new InputStreamReader(this.socket
				.getInputStream(), "UTF-8"));
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
				outputStream, 1024), true);
	}

	public void write(byte[] b) throws IOException {
		outputStream.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		outputStream.write(b, off, len);
	}

	protected String openSocketAndReadln() throws UnknownHostException,
			IOException {
		openSocket();
		return readln();
	}

	protected void closeSocket() {
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (Exception e) {
			this.socket = null;
		}
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
		debug("closeSocket - disconnected.");
	}

	protected void println(String s) {
		this.lnWriter.println(s);
		detail("-> " + s);
	}

	protected String readln() throws IOException {
		String s = this.lnReader.readLine();
		detail("<- " + s);
		return s;
	}

	public void sendCmd(String cmd) throws Exception {
		sendCmd(cmd, 0);
	}

	protected String oneCmd(String cmd) throws Exception {
		return sendCmd(cmd, 1)[0];
	}

	protected String[] sendCmd(String cmd, int readlnCount) throws Exception {
		List<String> list = new ArrayList<String>();
		println(cmd);
		for (int i = 0; i < readlnCount; i++) {
			String s = readln();
			list.add(s);
		}
		return list.toArray(new String[0]);
	}

	protected Map<String, String> sendCmds(String[] cmds) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		for (String cmd : cmds) {
			map.put(cmd, oneCmd(cmd));
		}
		return map;
	}

	public void ocSendCmd(String cmd) throws Exception {
		try {
			openSocket();
			sendCmd(cmd);
		} finally {
			closeSocket();
		}
	}

	/**
	 * Open socket and send command, finally close the socket
	 * 
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public String ocOneCmd(String cmd) throws Exception {
		return ocOneCmd(cmd, false);
	}

	/**
	 * Open socket and send command, finally close the socket.
	 * 
	 * @param cmd
	 * @param isReadln
	 *            if readline after connect
	 * @return
	 * @throws Exception
	 */
	public String ocOneCmd(String cmd, boolean isReadln) throws Exception {
		try {
			if (isReadln)
				openSocketAndReadln();
			else
				openSocket();
			return oneCmd(cmd);
		} finally {
			closeSocket();
		}

	}

	/**
	 * 向某服务器端口，发一条TCP指令，并取得回应
	 */
	public static String ocCmd(String host, int port, String cmd)
			throws Exception {
		AbsSocketClient c = new AbsSocketClient();
		c.setHost(host);
		c.setPort(port);
		c.setTimeoutSeconds(3000);
		return c.ocOneCmd(cmd, true);
	}
	
	/**
	 * 向某服务器端口，发一条TCP指令，并取得回应
	 */
	public static String ocCmd(InetSocketAddress addr, String cmd)
			throws Exception {
		AbsSocketClient c = new AbsSocketClient();
		c.setHost(addr.getAddress().getHostAddress());
		c.setPort(addr.getPort());
		c.setTimeoutSeconds(3000);
		return c.ocOneCmd(cmd, true);
	}
}
