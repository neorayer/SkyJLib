package com.skymiracle.server.udpServer;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;

public abstract class Commander {

	protected UdpCmdHandler cmdHandler;

	public Commander( UdpCmdHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}


	protected byte[] getBytes(String s) throws UnsupportedEncodingException {
		return s.getBytes(this.cmdHandler.getCharset());
	}

	protected byte[] getBytesCRLF(String s) throws UnsupportedEncodingException {
		return getBytes(s + "\r\n");
	}
//
//	protected byte[] getHelpBytesCRLF(String s)
//			throws UnsupportedEncodingException, ErrorCommanderClassException {
//		return getBytes("550 " + getCommanderName() + " " + s + "\r\n");
//	}
//
//	protected void println(String s) {
//		this.cmdHandler.lnWriter.println(s);
//	}
//	
//	protected void flush() {
//		this.cmdHandler.lnWriter.flush();
//	}
//
//	protected String readln() throws IOException {
//		return this.cmdHandler.lnReader.readLine();
//	}
//
//	protected InputStream getSocketInputStream() throws IOException {
//		return this.cmdHandler.getInputStream();
//	}
//
//	protected Socket getSocket() throws IOException {
//		return this.cmdHandler.getSocket();
//	}
//
//	protected OutputStream getSocketOutputStream() throws IOException {
//		return this.cmdHandler.getOutputStream();
//	}
//
//	protected CmdServer getCmdServer() {
//		return this.cmdHandler.cmdServer;
//	}
//
//	protected String getRemoteIP() {
//		return this.cmdHandler.getRemoteIP();
//	}
//
//	protected String getCommanderName() throws ErrorCommanderClassException {
//		return CmdServer.getCommanderName(this.getClass());
//	}
//
//	protected void setQuiting(boolean quiting) {
//		this.cmdHandler.setQuiting(quiting);
//	}
//	
	public  abstract byte[] doCmd(String[] args, DatagramPacket dPacket) throws Exception;

}
