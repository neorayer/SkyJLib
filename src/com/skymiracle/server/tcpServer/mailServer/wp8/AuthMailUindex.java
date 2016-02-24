package com.skymiracle.server.tcpServer.mailServer.wp8;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.skymiracle.auth.AuthMail;
import com.skymiracle.auth.MailUser;
import com.skymiracle.auth.Password;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UsernameWithDomain;

public class AuthMailUindex implements AuthMail {

	private String defaultDomain = null;

	String[] domains = null;

	public AuthMailUindex() throws IOException {

		loadDefaultDomain();
	}

	private void loadDefaultDomain() throws IOException {
		this.defaultDomain = TextFile.loadString(
				"/wdpost/conf/default.vdomain", "");
	}

	public MailUser authMail(String username, String domain, String password,
			String modeName, String remoteIP) {
		Uindex uIndex = new Uindex(username, domain);
		try {
			// 再如uindex信息
			uIndex.load();
		} catch (IOException e) {
			// IO异常，说明uindex文件读取失败，用户不存在
			return null;
		}

		// 密码校验
		String passwordDncrypted = uIndex.getPasswordEncrypted();
		Password pwd = new Password(password);
		if (!pwd.check(passwordDncrypted))
			return null;

		Wp8MailUser user = new Wp8MailUser(null);
		user.setUid(username.toLowerCase());
		user.setDc(domain.toLowerCase());
		user.setStatus(MailUser.STATUS_OPEN);
		user.setStorageLocation(uIndex.getHome());
		user.setMessageSize(1024 * 1024 * 1024);
		return user;
	}

	public MailUser getMailUser(String username, String domain)
			throws Exception {
		// 好像这个函数没有被POP3Server实用，只有SmtpServer用到了。
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasPermissionPop3(MailUser mailUser) {
		return true;
	}

	public boolean hasPermissionSmtp(String domain) {
		return true;
	}

	public boolean hasPermissionSmtp(MailUser<?> mailUser) {
		return true;
	}

	public void setMailUserAttr(MailUser<?> mailUser,
			Map<String, Object> attrMap) throws Exception {
		throw new Exception("Has not been implemented!");
	}

	public UsernameWithDomain auth(String username, String domain,
			String password, String modeName, String remoteIP) {
		throw new RuntimeException("Has not been implemented!");
	}

	public boolean chgPassword(String uid, String dc, String oldPass,
			String newPass) throws Exception {
		throw new Exception("Has not been implemented!");
	}

	public String getDefaultDomain() {
		return defaultDomain;

	}

	public boolean hasPermission(String username, String domain,
			String permissionName) throws Exception {
		return true;
	}


	public List<MailUser<?>> getGrpMembers(MailUser mailUser) {
		// TODO Auto-generated method stub
		return null;
	}

}
