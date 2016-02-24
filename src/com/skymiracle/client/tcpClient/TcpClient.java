package com.skymiracle.client.tcpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import com.skymiracle.client.Client;

public interface TcpClient extends Client {

	public void connection() throws UnknownHostException, IOException;

	public void disConnection() throws IOException;

	public void writeln(String string) throws IOException;

	public String readln() throws UnsupportedEncodingException, IOException;
}
