package com.skymiracle.server.tcpServer.mailServer.testCase;

import java.util.List;
import java.util.Map;

import com.skymiracle.auth.AuthMail;
import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;
import com.skymiracle.util.UsernameWithDomain;

public class AuthMailTest implements AuthMail {

	public MailUser authMail(String username, String domain, String password,
			String modeName, String remoteIP) {
		if ("test@test.com:111111"
				.equals((username + "@" + domain + ":" + password)))
			return getMailUser(username, domain);
		if ("trtr@test.com:111111"
				.equals((username + "@" + domain + ":" + password)))
			return getMailUser(username, domain);
		if ("sky@test1.com:111111"
				.equals((username + "@" + domain + ":" + password)))
			return getMailUser(username, domain);
		return null;
	}

	public MailUser getMailUser(String username, String domain) {
		MailUser mailUser = new TestMailUser(null);
		mailUser.setUid(username);
		mailUser.setDc(domain);
		mailUser.setSize(2000000);
		mailUser.setIssmtp("1");
		if (domain.equals("test.com") || domain.equals("test1.com")) {
			if (username.equals("test")){
				mailUser.setStorageLocation(Authable.LOCATION_NATIVE_LOCAL);
				mailUser.setAddrbookfilter(1);
			}
			else if (username.equals("trtr"))
				mailUser.setStorageLocation("127.0.0.1:6001");
			else if (username.equals("sky"))
				mailUser.setStorageLocation("127.0.0.1:6001"); 
			else mailUser.setStorageLocation(Authable.LOCATION_EXCEPTION);
		} else
			mailUser.setStorageLocation(Authable.LOCATION_FOREIGN);
		mailUser.setMaxcc(10);
		return mailUser;
	}

	public boolean hasPermissionPop3(MailUser mailUser) {
		return true;
	}

	public boolean hasPermissionSmtp(String domain) {
		return true;
	}

	public boolean hasPermissionSmtp(MailUser mailUser) {
		return true;
	}

	public UsernameWithDomain auth(String username, String domain,
			String password, String modeName, String remoteIP) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDefaultDomain() {
		return "test.com";
	}

	public boolean hasPermission(String username, String domain,
			String permissionName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMailUserAttr(MailUser mailUser, Map attrMap) {
		// TODO Auto-generated method stub
		
	}

	public boolean chgPassword(String uid, String dc, String oldPass,
			String newPass) throws Exception {
				return false;
		// TODO Auto-generated method stub
		
	}

	public List<MailUser<?>> getGrpMembers(MailUser mailUser) {
		// TODO Auto-generated method stub
		return null;
	}

}
