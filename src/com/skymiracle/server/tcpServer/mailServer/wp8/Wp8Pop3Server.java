package com.skymiracle.server.tcpServer.mailServer.wp8;

import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessorFactory;
import com.skymiracle.server.tcpServer.mailServer.Pop3.Pop3Server;

public class Wp8Pop3Server extends Pop3Server{

	public Wp8Pop3Server() throws Exception {
		super();
	
		AuthMailUindex authMail = new AuthMailUindex();
		this.setAuthMail(authMail);
		
		IMailAccessorFactory smaFactory = new Wp8StorageMailAccessorFactory();
		this.setStorageMailAccessorFactory(smaFactory);
	}

	public static void main(String[] args) throws Exception {
		Wp8Pop3Server s = new Wp8Pop3Server();
		s.start();
	}

}
