package com.skymiracle.mdo4.confDao;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;

public class ConfDao extends Dao {

	private String confID;

	public ConfDao() {

	}

	public ConfDao(String confID) {
		this.confID = confID;
	}

	public String getConfID() {
		return this.confID;
	}

	public void setConfID(String confID) {
		this.confID = confID;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "confID" };
	}

	@Override
	public String[] objectClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String selfDN() throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

}
