package com.skymiracle.server.tcpServer.httpServer.test;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.httpServer.ContextConf;
import com.skymiracle.server.tcpServer.httpServer.HttpServer;

public class TestHttpServer {
	public static void main(String[] args) throws Exception {
		Logger.setLevel(Logger.LEVEL_ERROR);
		HttpServer httpServer = new HttpServer();
		httpServer.setPort(812);
		httpServer.setCmdTimeoutSeconds(10);
		
		ContextConf cc = new ContextConf();
		cc.setName("test");
		cc.setDocRootPath("c:\\");
		httpServer.addContentConf(cc);
		
		httpServer.addSspDealer("/test/plus.ssp", new PlusSspDealer());
		
		httpServer.start();
	}
}
