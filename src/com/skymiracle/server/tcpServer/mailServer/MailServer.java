package com.skymiracle.server.tcpServer.mailServer;

import com.skymiracle.auth.AuthMail;
import com.skymiracle.auth.MailUser;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.tcpServer.TcpServerInfo;
import com.skymiracle.server.tcpServer.antiCracker.AntiCracker;
import com.skymiracle.server.tcpServer.antiCracker.NoneAntiCracker;
import com.skymiracle.server.tcpServer.cmdServer.CmdServer;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessorFactory;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;
import com.skymiracle.util.UsernameWithDomain;

public abstract class MailServer extends CmdServer {

	private AuthMail authMail;

	private IMailAccessorFactory mailAccessorFactory;

	private AntiCracker antiCracker = new NoneAntiCracker();

	public MailServer(String name, int port) throws Exception {
		super(name, port);
	}

	public AuthMail getAuthMail() {
		return authMail;
	}

	public void setAuthMail(AuthMail authMail) {
		this.authMail = authMail;
	}

	public IMailAccessorFactory getStorageMailAccessorFactory() {
		return this.mailAccessorFactory;
	}

	public void setStorageMailAccessorFactory(
			IMailAccessorFactory storageMailAccessorFactory) {
		this.mailAccessorFactory = storageMailAccessorFactory;
	}

	public AntiCracker getAntiCracker() {
		return antiCracker;
	}

	public void setAntiCracker(AntiCracker antiCracker) {
		this.antiCracker = antiCracker;
	}

	public MailUser<?> authMail(String authUsername, String password,
			String modeName, String remoteIP) {
		UsernameWithDomain uwd = new UsernameWithDomain(authUsername,
				getDefaultDomain());
		MailUser mailUser = authMail.authMail(uwd.getUsername(), uwd.getDomain(), password,
				modeName, remoteIP);
		antiCracker.doFilterAfterAuth(mailUser != null, authUsername, modeName, remoteIP);
		return mailUser;
	}

	public IMailAccessor getUsMailAccessor(String username, String domain,
			String location) throws Exception {
		return mailAccessorFactory.getUserStorageMailAccessor(username, domain,
				location);
	}

	public String getDefaultDomain() {
		return authMail.getDefaultDomain();
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		return new TcpServerInfo();
	}

	public MailUser<?> getMailUser(String username, String domain)
			throws Exception {
		MailUser<?> mailUser = authMail.getMailUser(username, domain);
		return mailUser;
	}

}
