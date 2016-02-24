package com.skymiracle.auth;

import com.skymiracle.client.tcpClient.AbsSocketClient;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UsernameWithDomain;

public class PopAuthImpl implements Authable {

	private String pop3Host = "mail.skymiracle.com";
	
	private int pop3Port = 110;
	
	private String defaultDomain = "skymiracle.com";
	
	public UsernameWithDomain auth(String username, String domain,
			String password, String modeName, String remoteIP) {
		InnerPop3Client ipc = new InnerPop3Client(this.pop3Host, this.pop3Port);
		try {
			if (!ipc.auth(username, password))
					return null;
			else
				return new UsernameWithDomain(username +'@' + domain, this.defaultDomain);
		} catch (Exception e) {
			Logger.error("", e);
			return null;
		}
	}

	public String getPop3Host() {
		return pop3Host;
	}

	public void setPop3Host(String pop3Host) {
		this.pop3Host = pop3Host;
	}

	public int getPop3Port() {
		return pop3Port;
	}

	public void setPop3Port(int pop3Port) {
		this.pop3Port = pop3Port;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	public boolean hasPermission(String username, String domain,
			String permissionName) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class InnerPop3Client extends AbsSocketClient {
		
		public InnerPop3Client(String host, int port) {
			setHost(host);
			setPort(port);
		}
		
		public boolean auth(String username, String password) throws Exception {
			openSocketAndReadln();
			String s = oneCmd("user " + username);
			if (s== null)
				return false;
			if (!s.startsWith("+"))
				return false;
			
			s = oneCmd("pass " + password);
			if (s== null)
				return false;
			if (!s.startsWith("+"))
				return false;
		
			oneCmd("quit");
			closeSocket();
			return true;
		}
	}

	public boolean chgPassword(String uid, String dc, String oldPass,
			String newPass) throws Exception {
		throw new Exception("Can not implement in PopAuthImpl");
	}

}
