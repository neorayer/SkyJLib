package com.skymiracle.mdo4.testCase;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;

public class GlobalAdmin extends Dao {

	private String uid;

	private String userPassword;

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		return baseDN;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "uid" };
	}

	@Override
	public String[] objectClasses() {
		return new String[] { "mailPerson", "inetOrgPerson", "country" };
	}

	@Override
	public String selfDN() throws NullKeyException {
		return "uid=" + this.uid;
	}

	@Override
	public String table() {
		return "tb_admin";
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

}
