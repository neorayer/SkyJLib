package com.skymiracle.server.tcpServer.httpServer;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;

public class ContextConf extends Dao{

	private String name = "";
	
	private String docRootPath = "/tmp/";
	
	public String getDocRootPath() {
		return docRootPath;
	}

	public void setDocRootPath(String docRootPath) {
		this.docRootPath = docRootPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] keyNames() {
		// TODO Auto-generated method stub
		return null;
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
