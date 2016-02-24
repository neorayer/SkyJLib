package com.skymiracle.mdo4.someDao;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;

public class LdapODao extends Dao {

	private String o;

	public String getO() {
		return this.o;
	}

	public void setO(String o) {
		this.o = o;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		return baseDN;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "o" };
	}

	@Override
	public String[] objectClasses() {
		return new String[] { "organization" };
	}

	@Override
	public String selfDN() throws NullKeyException {
		return "o=" + this.o;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

}
