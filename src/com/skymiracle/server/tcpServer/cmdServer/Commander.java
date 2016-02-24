package com.skymiracle.server.tcpServer.cmdServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Commander {

	protected CmdConnHandler connHandler;

	public Commander(CmdConnHandler connHandler) {
		this.connHandler = connHandler;
	}

	protected byte[] getBytes(String s) throws UnsupportedEncodingException {
		return s.getBytes(this.connHandler.getCharset());
	}

	protected byte[] getBytesCRLF(String s) throws UnsupportedEncodingException {
		return getBytes(s + "\r\n");
	}

	protected byte[] getHelpBytesCRLF(String s)
			throws UnsupportedEncodingException, ErrorCommanderClassException {
		return getBytes("550 " + getCommanderName() + " " + s + "\r\n");
	}

	protected void println(String s) {
		this.connHandler.getLnWriter().println(s);
	}
	
	protected void flush() {
		this.connHandler.getLnWriter().flush();
	}

	protected String readln() throws IOException {
		return this.connHandler.getLnReader().readLine();
	}

	protected InputStream getSocketInputStream() throws IOException {
		return this.connHandler.getInputStream();
	}

	protected Socket getSocket() throws IOException {
		return this.connHandler.getSocket();
	}

	protected InetSocketAddress getInetSocketAddress() throws IOException {
		return (InetSocketAddress) getSocket().getRemoteSocketAddress();
	}
	protected OutputStream getSocketOutputStream() throws IOException {
		return this.connHandler.getOutputStream();
	}

	protected CmdServer getCmdServer() {
		return this.connHandler.getCmdServer();
	}

	protected String getRemoteIP() {
		return this.connHandler.getRemoteIP();
	}

	protected int getRemotePort() {
		return this.connHandler.getRemotePort();
	}

	protected String getCommanderName() throws ErrorCommanderClassException {
		return CmdServer.getCommanderName(this.getClass());
	}

	protected void setQuiting(boolean quiting) {
		this.connHandler.setQuiting(quiting);
	}
	
	public abstract byte[] doCmd(String head, String tail) throws Exception;

}
