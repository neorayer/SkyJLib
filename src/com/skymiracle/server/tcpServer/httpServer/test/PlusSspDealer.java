package com.skymiracle.server.tcpServer.httpServer.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import com.skymiracle.server.tcpServer.httpServer.GetMethodSspDealer;

public class PlusSspDealer extends GetMethodSspDealer {

	@Override
	protected void deal(Socket socket, String path, Map<String, String> paraMap)
			throws IOException {
		OutputStream os = socket.getOutputStream();
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.0 200 OK\r\n");
		sb.append("Content-type: text/html\r\n");
		sb.append("Server: Sky HttpServer\r\n");
		os.write(sb.toString().getBytes());
		os.write("\r\n".getBytes());

		os.write("asdfadsf\r\n".getBytes());
	}

}
