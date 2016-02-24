package com.skymiracle.auth;

import java.io.IOException;

import com.skymiracle.io.TextFile;
import com.skymiracle.util.UsernameWithDomain;

@SuppressWarnings("deprecation")
public class SimplePlainAuthImpl implements Authable {

	private String filePath = "/tmp/simpleauth.txt";

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDefaultDomain() {
		return null;
	}

	public UsernameWithDomain auth(String username, String domain,
			String password, String modeName, String remoteIP) {
		try {
			String[] lines = TextFile.loadLines(this.filePath);
			for (int i = 0; i < lines.length; i++) {
				String[] words = lines[i].split(":");
				String _username = words[0];
				String _domain = words[1];
				String _password = words[2];
				if (_username.equals(username) && _domain.equals(domain)) {
					if (_password.equals(password)) {
						UsernameWithDomain uwd = new UsernameWithDomain(
								username, domain);
						return uwd;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasPermission(String username, String domain,
			String permissionName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean chgPassword(String uid, String dc, String oldPass,
			String newPass) throws Exception {
				return false;
		// TODO Auto-generated method stub
		
	}

}
