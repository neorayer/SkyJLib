package com.skymiracle.client.tcpClient.smtpClient;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.skymiracle.client.tcpClient.smtpClient.SmtpSendResult.ResultItem;
import com.skymiracle.logger.Logger;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;
import com.skymiracle.util.Base64;

public class SmtpSendSession {
	private int timeout = 300000;

	private String smtpHost = "127.0.0.1";

	private int smtpPort = 25;

	// ////////////

	private Socket socket;

	private InternetPrintWriter lnWriter;

	private CRLFTerminatedReader lnReader;
	
	public SmtpSendSession() {}

	public SmtpSendSession(String smtpHost, int smtpPort) {
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	} // ///////////////////////////////////////////////

	public void open() throws UnknownHostException, IOException, SmtpSendException {
		//rset();
		Logger.debug("SmtpSendSession: connect " + smtpHost + ":" + smtpPort);

		this.socket = new Socket(smtpHost, smtpPort);
		this.socket.setSoTimeout(this.timeout);
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
				this.socket.getOutputStream(), 1024), true);
		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
				"ASCII");
		readLine();
	}

	public void close() {
		if (this.socket == null)
			return;
		try {
			Logger.debug("SmtpSendSession: disconnect");
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private String readLine() throws IOException, SmtpSendException {
		String resp = "";
		String s = this.lnReader.readLine();
		if (s == null) {
			Logger.error("SmtpSendSession: < null");
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, "SmtpSendSession.readline() is null");
		}
		resp += s;
		while (true) {
			if (s.indexOf('-') == 3)
				try {
					s = this.lnReader.readLine();
					resp += s;
				} catch (IOException e) {
					e.printStackTrace();
				}
			else
				break;
		}
		Logger.detail("SmtpSendSession: < " + resp);
		return resp;
	}

	private void println(String s) throws IOException {
		Logger.detail("SmtpSendSession: > " + s);
		this.lnWriter.println(s);
	}

	public void helo(String helo) throws IOException, SmtpSendException {
		println("HELO " + helo);
		String s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("2"))
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);

	}

	public void ehlo(String helo) throws IOException, SmtpSendException {
		println("EHLO " + helo);
		String s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("2"))
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);
	}

	public void auth(String username, String password) throws IOException,
			SmtpSendException {
		println("AUTH LOGIN " + Base64.encodeToString(username, "ASCII"));
		String s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("3")) {
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);
		}
		println(Base64.encodeToString("" + password, "ASCII"));
		s = readLine();
		if (!s.startsWith("2"))
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);

	}

	public void mail(String fromEmail) throws IOException, SmtpSendException {
		println("MAIL FROM:<" + fromEmail + ">");
		String s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("2")) {
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);
		}
	}

	public void rcpt(String[] toEmails, SmtpSendResult res) throws IOException,
			SmtpSendException {
		int succCount = 0;
		for (String toEmail : toEmails) {
			println("RCPT TO:<" + toEmail + ">");
			String s = readLine();

			if (s.startsWith("4")) {
				res.put(toEmail, new SmtpSendException(
						ResultItem.TYPE_SOFT_ERR, s));
			} else if (s.startsWith("5")) {
				res.put(toEmail, new SmtpSendException(
						ResultItem.TYPE_HARD_ERR, s));
			} else if (!s.startsWith("2")) {
				res.put(toEmail, new SmtpSendException(
						ResultItem.TYPE_HARD_ERR, s));
			} else  if (s.startsWith("2")){
				succCount++;
			}
		}
		if (succCount == 0)
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR,
					(String)null);
	}

	public void data(List<String> dataLineList) throws IOException,
			SmtpSendException {
		// data
		println("DATA");
		String s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("3")) {
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);
		}
		int byteCount = 0;
		for (String dataLine : dataLineList) {
			this.lnWriter.println(dataLine);
			byteCount += dataLine.length();
		}
		Logger.detail("SmtpSendSession: data sent " + byteCount + " words");

		println("");
		println(".");
		this.lnWriter.flush();

		s = readLine();
		if (s.startsWith("4"))
			throw new SmtpSendException(ResultItem.TYPE_SOFT_ERR, s);
		if (!s.startsWith("2")) {
			throw new SmtpSendException(ResultItem.TYPE_HARD_ERR, s);
		}
	}
	
	public void quit() throws IOException {
		println("QUIT");
	}
	


}
