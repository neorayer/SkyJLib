package com.skymiracle.server.udpServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.ServerImpl;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.util.ByteUtils;

public class UdpServer extends ServerImpl {

	protected int port = 6666;

	protected DatagramSocket dSocket;

	private Class dataRecvHandlerClass;
	
	private long timeoutMills = 30000;
	
	private ExecutorService exe;
	
	private boolean isShutdown = false;
	
	public UdpServer(String name, int port, Class dataRecvHandlerClass) {
		super(name);
		this.port = port;
		this.dataRecvHandlerClass = dataRecvHandlerClass;
		exe = Executors.newCachedThreadPool();
	}

	public long getTimeoutMills() {
		return timeoutMills;
	}

	public void setTimeoutMills(long timeoutMills) {
		this.timeoutMills = timeoutMills;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Class getDataRecvHandlerClass() {
		return dataRecvHandlerClass;
	}

	public void setDataRecvHandlerClass(Class dataRecvHandlerClass) {
		this.dataRecvHandlerClass = dataRecvHandlerClass;
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public void start() {
		isShutdown = false;
		Thread thread = new Thread(this);
		thread.setName(new StringBuffer("UdpServer Listenning Thread:").append(
				this.name).append(" - Port:").append(this.port).toString());
		thread.start();
	}

	public void stop() {
		isShutdown = true;
		if (dSocket != null)
			dSocket.close();
		exe.shutdown();
	}

	public void run() {
		try {
			dSocket = new DatagramSocket(port);
			Logger.info("Udp Server [" + this.name + "] start at " + port);
			final UdpServer udpServer = this;
			int rLen = 0;
			while (!isShutdown) {
				
				final UdpRecvHandler recvHandler = dataRecvHandlerClass == null ? new DebugUdpRecvHandler()
				: (UdpRecvHandler) dataRecvHandlerClass.newInstance();
				byte[] rb = new byte[4096];
				final DatagramPacket dPacket = new DatagramPacket(rb, rb.length);

				try {
					dSocket.receive(dPacket);
				}catch(SocketException e) {
					//如果receive异常，首先检查是否isShutdown被置为true
					if (isShutdown) //说明是server主动关闭，跳出循环，并忽略“socket closed”这个异常。
						break;

					throw e;
				}
				rLen = dPacket.getLength();
				if (rLen > 0) {
					exe.execute(new Runnable() {
						public void run() {
							try {
								recvHandler.handleDataRecv(udpServer, dPacket);
							} catch (Exception e) {
								Logger.error("", e);
							}
						}
					});
				}

				continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	



	public class DebugUdpRecvHandler extends UdpRecvHandler {

		@Override
		public void handleDataRecv() throws Exception {
			Logger.debug("Received Len=" + this.dPacket.getLength() + " "
					+ this.dPacket.getAddress().getHostAddress() + ":"
					+ this.dPacket.getPort());
			System.out.println(ByteUtils.bs2Hex(this.dPacket.getData(),
					this.dPacket.getLength()));
		}

	}
	
	public DatagramSocket getDSocket() {
		return dSocket;
	}


}
