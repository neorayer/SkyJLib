package com.skymiracle.server.tcpServer.httpServer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.TcpConnHandler;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;

public class HttpHandler extends TcpConnHandler<HttpServer> {
//	protected CRLFTerminatedReader lnReader;
//
//	protected InternetPrintWriter lnWriter;

	private String method = "GET";

	private String requestUri = "/";

	private String requestPath = "/";

	private String context = "/";

	private String ext = "";

	private String path = "";

	private String query = "";

	@Override
	public void handleConnection() throws Exception {
//		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
//				"UTF-8");
//		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
//				this.socket.getOutputStream(), 1024), true);

		String s = this.lnReader.readLine();
		Logger.detail(s);
		parseFirstLine(s);

		// read request head
		for (int i = 0; i < 100; i++) {
			String l = this.lnReader.readLine();
			Logger.detail(l);
			if (l.trim().length() == 0)
				break;
		}

		ContextConf contextConf = getContextConf(context);
		if (contextConf == null) {
			outputNoFound();
			return;
		}
		if (ext.equals("ssp")) {
			dealWithSsp(path, query);
			return;
		}

		dealWithFile(contextConf);
	}

	private void dealWithFile(ContextConf contextConf) {
		outputHead();

		File file = new File(contextConf.getDocRootPath(), requestPath);
		// lnWriter.println("aa");
		// if (true)return;

		try {
			StreamPipe.fileToOutput(file, getOutputStream(), true);
		} catch (Exception e) {
			lnWriter.println(e.getMessage());
		}
	}

	private void dealWithSsp(String path, String query) throws IOException {
	
		SspDealer sspDealer = getSspDealer(path);
		if (sspDealer == null) {
			outputStandardError(new Exception("No Such Ssp dealer:" + path));
			return;
		}
		try {
			sspDealer.deal(this.socket, path, query);
		} catch (Exception e) {
			outputStandardError(e);
		}
	}

	private SspDealer getSspDealer(String path) {
		return this.tcpServer.getSspDealer(path);
	}

	private void outputHead() {
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.0 200 OK\r\n");
		sb.append("Content-type: text/html\r\n");
		sb.append("Server: Sky HttpServer\r\n");
		lnWriter.println(sb.toString());
	}

	private void outputStandardError(Exception e) throws IOException {
		outputHead();
		e.printStackTrace(new PrintStream(socket.getOutputStream()));
	}

	private void outputText(String s) {
		outputHead();
		lnWriter.println(s);
	}

	private void outputNoFound() {
		outputHead();

		lnWriter.println("<h1>No Found!</h1>");

	}

	private ContextConf getContextConf(String context) {
		return this.tcpServer.getContextConf(context);
	}

	private void parseFirstLine(String s) throws Exception {
		{
			String[] ss = s.split(" ");
			if (ss.length < 2)
				throw new Exception("Error request format");

			method = ss[0];
			requestUri = ss[1];
		}
		{
			String[] ss = requestUri.split("/");
			if (ss.length > 1) {
				context = ss[1];
				requestPath = requestUri.substring(context.length() + 1);
				if (requestPath.length() >= 1)
					if (requestPath.charAt(0) == '/')
						requestPath = requestPath.substring(1);
			} else {
				context = "";
				requestPath = "";
			}
		}
		Logger.detail("method=" + method);
		Logger.detail("requestUri=" + requestUri);
		Logger.detail("context=" + context);
		Logger.detail("requestPath=" + requestPath);

		URL url = new URL("http://localhost" + requestUri);
		{
			path = url.getPath();
			query = url.getQuery();
			Logger.detail("path=" + path);
			Logger.detail("query=" + query);

			String[] ss = path.split("/");
			if (ss.length > 0) {
				String last = ss[ss.length - 1];
				int i = last.lastIndexOf('.');
				if (i < 0)
					ext = "";
				else
					ext = last.substring(i + 1);
			} else {
				ext = "";
			}
			Logger.detail("ext=" + ext);
		}
	}
}
