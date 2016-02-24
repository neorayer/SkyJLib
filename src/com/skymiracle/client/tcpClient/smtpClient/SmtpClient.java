package com.skymiracle.client.tcpClient.smtpClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;

import com.skymiracle.client.tcpClient.smtpClient.SmtpSendResult.ResultItem;
import com.skymiracle.dns.Dns;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UsernameWithDomain;

/**
 * A SmtpClient implement. We can send a email with SMTP by it.
 * 
 * @author zhourui
 * 
 */
public class SmtpClient {
	private String helo = "localhost";

	private String fromEmail;

	private String username;

	private String password;

	private int timeout = 30000;

	private int retryMax = 10;

	private long retrySleep = 10000;

	private String smtpHost = "127.0.0.1";

	private int smtpPort = 25;

	public SmtpClient() {
		super();

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
	}

	public String getFromEmail() {
		return this.fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRetryMax() {
		return this.retryMax;
	}

	public void setRetryMax(int retryMax) {
		this.retryMax = retryMax;
	}

	public long getRetrySleep() {
		return this.retrySleep;
	}

	public void setRetrySleep(long retrySleep) {
		this.retrySleep = retrySleep;
	}

	public void setTimeout(int timeoutMillsSeconds) {
		this.timeout = timeoutMillsSeconds;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public String getHelo() {
		return this.helo;
	}

	public void setHelo(String helo) {
		this.helo = helo;
	}

	public SmtpSendResult send(String toEmail, List<String> dataLineList) {
		UsernameWithDomain uwd = new UsernameWithDomain(toEmail, "");
		String mxDomain = uwd.getDomain();

		try {
			String[] mxHosts = Dns.resolveMxOrARecord(mxDomain);
			return send(mxHosts, toEmail, dataLineList);
		} catch (NamingException e) {
			SmtpSendResult res = new SmtpSendResult(new String[] { toEmail });
			res.setAllRes(ResultItem.TYPE_HARD_ERR, e.getMessage());
			return res;
		}
	}

	/**
	 * 发送邮件，并且强制外发，即目标地址不在本机(127.0.0.1, 0.0.0.0, 本机IP)
	 * 
	 * @param toEmail
	 * @param dataLineList
	 * @return
	 */
	public SmtpSendResult send(String toEmail, List<String> dataLineList,
			boolean isForeignForce) {
		UsernameWithDomain uwd = new UsernameWithDomain(toEmail, "");
		String mxDomain = uwd.getDomain();

		try {
			String[] mxHosts = Dns.resolveMxOrARecord(mxDomain);
			return send(mxHosts, toEmail, dataLineList, isForeignForce);
		} catch (NamingException e) {
			SmtpSendResult res = new SmtpSendResult(new String[] { toEmail });
			res.setAllRes(ResultItem.TYPE_HARD_ERR, e.getMessage());
			return res;
		}
	}

	public SmtpSendResult send(String[] hosts, String toEmail,
			List<String> dataLineList) {
		return send(hosts, toEmail, dataLineList, false);
	}

	public SmtpSendResult send(String[] hosts, String toEmail,
			List<String> dataLineList, boolean isForeignForce) {
		SmtpSendResult res = new SmtpSendResult(new String[] { toEmail });

		for (String host : hosts) {
			// 强制指定外发邮件。
			if (isForeignForce) {
				// 防止loop
				String ip = null;
				try {
					ip = InetAddress.getByName(host).getHostAddress();
				} catch (UnknownHostException e) {
				}
				if ("127.0.0.1".equals(ip) || "0.0.0.0".equals(ip)) {
					res.setAllRes(ResultItem.TYPE_HARD_ERR, "domain["
							+ smtpHost + "]" + " is loop.");

					return res;
				}
			}

			res = send(host, 25, toEmail, dataLineList);
			if (res.getResultType() == SmtpSendResult.RESTYPE_ALLSUCC)
				return res;
		}
		return res;
	}

	public SmtpSendResult sendWithRoute(String toEmail,
			List<String> dataLineList) {
		return send(this.smtpHost, this.smtpPort, toEmail, dataLineList);
	}

	public SmtpSendResult send(String host, int port, String toEmail,
			List<String> dataLineList) {
		return send(host, port, new String[] { toEmail }, dataLineList);
	}

	public SmtpSendResult sendWithRoute(List<String> toEmails,
			List<String> dataLineList) {
		return send(this.smtpHost, this.smtpPort, toEmails, dataLineList);
	}

	public SmtpSendResult send(String host, int port, List<String> toEmails,
			List<String> dataLineList) {
		return send(host, port, toEmails.toArray(new String[0]), dataLineList);
	}

	public SmtpSendResult send(String host, int port, String[] toEmails,
			List<String> dataLineList) {
		return send(host, port, this.username, this.password, toEmails,
				dataLineList);
	}

	public SmtpSendResult send(String host, int port, String username,
			String password, String[] toEmails, List<String> dataLineList) {
		SmtpSendResult res = new SmtpSendResult(toEmails);
		SmtpSendSession s = new SmtpSendSession(host, port);
		s.setTimeout(this.timeout);
		try {
			s.open();
			s.helo(helo);

			if (username != null) {
				s.auth(username, password);
			}

			s.mail(fromEmail);
			s.rcpt(toEmails, res);
			s.data(dataLineList);
			s.quit();
		} catch (IOException e) {
			Logger.error("", e);
			res.setAllRes(ResultItem.TYPE_HARD_ERR, e.getMessage());
		} catch (SmtpSendException e) {
			res.setAllRes(e);
		} finally {
			s.close();
		}
		return res;

	}
}
