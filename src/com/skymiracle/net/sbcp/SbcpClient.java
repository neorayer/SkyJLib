package com.skymiracle.net.sbcp;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import com.skymiracle.util.ByteUtils;

/**
 */
public class SbcpClient {

	private String host;

	private int port;

	private int connTimeout = 3000;

	private Socket socket;

	private InputStream is;

	private OutputStream os;
	
	private int headTag;
	
	private int lastRecvLen = 0;
	
	protected byte[] recvBuf = new byte[1024];
	
	public SbcpClient() throws IOException {
		 socket = new Socket();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public int getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}

	public void connect() throws IOException {
		socket.connect(new InetSocketAddress(host, port), connTimeout);
		socket.setSoTimeout(3000);
		 is = socket.getInputStream();
		 os = socket.getOutputStream();
	}

	public void send(int typeByte, byte[] data) throws IOException {
		os.write(this.headTag);
		os.write(typeByte);
		os.write(ByteUtils.ushort2bs(data.length));
		os.write(data);
	}
	
	private void recv(int len) throws IOException {
		int c = 0;
		int rlen = 0;
		for(;c < len;) {
			rlen = is.read(recvBuf, c, len -c);
			if (rlen < 0)
				throw new IOException("not received enough bytes.");
			c += rlen;
		}
	}
	

	private int readShort() throws IOException {
		recv(2);
		return ByteUtils.bs2ushort(recvBuf, 0);
	}
	
	public int recv() throws IOException {
		System.out.println("begin recv");
		int ht = is.read() ;
		if (ht != headTag)
			throw new IOException("Error head Tag");
		int typeByte = is.read();
		int len = readShort();
		if (len <= 0)
			throw new IOException("data len is error. len=" + len);
		recv(len);
		this.lastRecvLen = len;
		return typeByte;
	}
	
	public void talk() throws IOException {
		for(;;) {
			int typeByte = recv();
			System.out.println("headTag=" + headTag + ", type=" + typeByte + ", dataLen=" + this.lastRecvLen);
		}
	}
	
	public void hello() throws IOException {
		send('a', new byte[1000]);
	}
	
	public static void main(String... args) throws Exception {
		SbcpClient c = new SbcpClient();
		c.setHost("127.0.0.1");
		c.setPort(8001);
		c.setConnTimeout(3000);
	

		
		c.connect();
		
		c.hello();
		c.talk();
	}

}
