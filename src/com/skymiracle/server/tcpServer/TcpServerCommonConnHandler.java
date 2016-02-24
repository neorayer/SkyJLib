package com.skymiracle.server.tcpServer;

import java.io.BufferedOutputStream;
import java.lang.reflect.Method;

import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;

public abstract class TcpServerCommonConnHandler<T extends TcpServer> extends
		TcpConnHandler<T> {
//	protected CRLFTerminatedReader lnReader;
//
//	protected InternetPrintWriter lnWriter;

	protected String s, sHead, sTail;

	@Override
	public void handleConnection() throws Exception {
//		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
//				"UTF-8");
//		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
//				this.socket.getOutputStream(), 1024), true);
		this.lnWriter.println(this.getWelcomeMessage());

		this.s = this.lnReader.readLine();

		if (this.s != null) {
			this.tcpServer.debug("<- " + this.s);
			this.sHead = this.s.toLowerCase();
			int pos = this.s.indexOf(' ');
			if (pos > 0) {
				this.sHead = this.s.substring(0, pos).toLowerCase();
				this.sTail = this.s.substring(pos + 1);
			} else
				this.sTail = "";

			StringBuffer sb = doCmd();

			if (sb != null) {
				this.lnWriter.println(sb.toString());
				this.tcpServer.debug("-> " + sb.toString());
			}
		}

//		this.lnReader.close();
//		this.lnWriter.close();
//		this.socket.close();

	}

	private StringBuffer doCmd() throws Exception {
		Method[] methods = this.getClass().getMethods();
		Method method = null;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType() != StringBuffer.class)
				continue;
			if (!methods[i].getName().startsWith(
					"doCmd_" + this.sHead.toUpperCase()))
				continue;
			method = methods[i];
			break;
		}
		if (method == null)
			return new StringBuffer(this.getUnknowCmdMessage());

		return (StringBuffer) method.invoke(this, new Object[0]);
	}

	protected abstract String getUnknowCmdMessage();

	protected abstract String getWelcomeMessage();

}
