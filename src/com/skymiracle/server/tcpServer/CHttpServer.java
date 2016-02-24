package com.skymiracle.server.tcpServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdServer.CmdServer;
import com.skymiracle.server.tcpServer.cmdServer.Commander;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public abstract class CHttpServer extends CmdServer{

	public CHttpServer(String name, int port) throws Exception {
		super(name, port);

		this.setWelcome(null);
		setCloseSocketAfterHandle(true);
		setShortConn(true);
		
		addCommander(GetCommander.class);
		addCommander(PostCommander.class);
		addCommander(OptionsCommander.class);
	}
	


	private static Map<String,String>  getParamsFromURL(URL url) throws MalformedURLException {
		Map<String,String> params = new HashMap<String,String>();
		String q = url.getQuery();
		if (q == null)
			return params;
		String[] ss = q.split("&");
		if (ss.length < 2)
			return params;
		for(String s1: ss) {
			String[] pp = s1.split("=");
			String k = pp[0];
			String v = pp[1];
			params.put(k,v);
		}
		return params;
	}
	
	public static class GetCommander extends Commander {

		public GetCommander(CmdConnHandler connHandler) {
			super(connHandler);
		}

		@Override
		public byte[] doCmd(String head, String tail) throws Exception {
			String[] ss = tail.split(" ");
			String urlstr = ss[0].trim();

			if (!urlstr.toLowerCase().startsWith("http://"))
				urlstr = "http://" + urlstr;
			URL url = new URL(urlstr);

			String path = url.getPath();
			Map<String,String> params = getParamsFromURL(url);

			return ((CHttpServer)getCmdServer()).dealRequest(path, params);
		}
	}
	
	public static class PostCommander extends Commander {

		public PostCommander(CmdConnHandler connHandler) {
			super(connHandler);
		}

		@Override
		public byte[] doCmd(String head, String tail) throws Exception {
			String[] ss = tail.split(" ");
			String urlstr = ss[0].trim();

			if (!urlstr.toLowerCase().startsWith("http://"))
				urlstr = "http://" + urlstr;
			URL url = new URL(urlstr);

			String path = url.getPath();
			Map<String,String> params = getParamsFromURL(url);

			return ((CHttpServer)getCmdServer()).dealRequest(path, params);
		}
	}
	public static class OptionsCommander extends Commander {

		public OptionsCommander(CmdConnHandler connHandler) {
			super(connHandler);
		}

		@Override
		public byte[] doCmd(String head, String tail) throws Exception {
			String s = "HTTP/1.0 200 OK\r\n" +
					"Date: Fri, 13 Mar 2009 06:10:03 GMT\r\n" +
					"Content-Length: 0\r\n" +
					"Allow: GET\r\n" +
					"Connection: close\r\n\r\n";
			return s.getBytes();
		}
	}
	
	protected abstract byte[] dealRequest(String path, Map<String, String> params) throws Exception;

}
