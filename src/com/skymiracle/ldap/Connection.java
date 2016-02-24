package com.skymiracle.ldap;

import java.io.UnsupportedEncodingException;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

public class Connection {

	public static LDAPConnection getCon(String host, int port, String dn,
			String password) throws LDAPException,
			UnsupportedEncodingException, InterruptedException {
		LDAPConnection lc = new LDAPConnection();
		lc.connect(host, port);
		lc.bind(LDAPConnection.LDAP_V3, dn, password.getBytes("UTF8"));
		return lc;
	}

	public static void releaseCon(LDAPConnection con) throws LDAPException {
		if (con == null)
			return;
		con.disconnect();
	}
}
