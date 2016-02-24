package com.skymiracle.server.tcpServer.httpServer;

import java.io.IOException;
import java.net.Socket;

public interface SspDealer {
	
	public void deal(Socket socket, String path, String query) throws IOException, Exception;

}
