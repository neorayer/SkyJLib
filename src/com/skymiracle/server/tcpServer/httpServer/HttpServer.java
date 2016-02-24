package com.skymiracle.server.tcpServer.httpServer;

import java.util.HashMap;
import java.util.Map;

import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.tcpServer.TcpServer;

public class HttpServer extends TcpServer {

	Map<String, SspDealer> sspDealerMap = new HashMap<String, SspDealer>();
	Map<String, ContextConf> contextConfMap = new HashMap<String, ContextConf>();

	public HttpServer() throws Exception {
		super("SkyHttpServer", 80, HttpHandler.class);
		this.setCmdTimeoutSeconds(3);
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSspDealer(String path, SspDealer sspDealer) {
		sspDealerMap.put(path.toLowerCase(), sspDealer);
	}

	public SspDealer getSspDealer(String path) {
		return sspDealerMap.get(path.toLowerCase());
	}

	public ContextConf getContextConf(String contextName) {
		return contextConfMap.get(contextName);
	}

	public void addContentConf(ContextConf cc) {
		contextConfMap.put(cc.getName(), cc);
	}



}
