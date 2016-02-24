package com.skymiracle.client.tcpClient.managerClient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.client.tcpClient.TcpClient;
import com.skymiracle.client.tcpClient.TcpClientAbs;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.queue.QueueInfo;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.ServersInfo;
import com.skymiracle.sysinfo.JvmMemInfo;
import com.skymiracle.sysinfo.OsInfo;
import com.skymiracle.sysinfo.SysInfo;

public class ManagerClient extends TcpClientAbs implements TcpClient {

	private String username;

	private String password;

	private String readLineStr;

	public ManagerClient(String host, int port, String username, String password) {
		super();

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	private void readCheckOK() throws Exception {
		this.readLineStr = this.lnReader.readLine();
		if (this.readLineStr == null)
			throw new IOException("socket read nothing");
		if (!this.readLineStr.startsWith("+OK"))
			throw new Exception(this.readLineStr);
	}

	private void login() throws Exception {
		this.lnReader.readLine();
		this.lnWriter.print("user " + this.username + "\r\n");
		this.lnWriter.flush();
		readCheckOK();
		this.lnWriter.print("pass " + this.password + "\r\n");
		this.lnWriter.flush();
		readCheckOK();
	}

	protected Object getObject(String cmd) throws Exception {
		try {
			connection();
			login();
			this.lnWriter.print(cmd + "\r\n");
			this.lnWriter.flush();
			return StreamPipe.inputToObject(this.socket.getInputStream(), true);
		} catch (Exception e) {
			Logger.info("ManagerClient.getObject(), username=" + this.username
					+ "password" + this.password, e);
		} finally {
			disConnection();
		}
		return null;

	}

	public void sendCmd(String cmd) throws IOException {
		try {
			connection();
			login();
			this.lnWriter.print(cmd + "\r\n");
			this.lnWriter.flush();
		} catch (Exception e) {
			Logger.info("ManagerClient.getObject(), username=" + this.username
					+ "password" + this.password, e);
		} finally {
			disConnection();
		}
	}

	public void sendShutdown() throws IOException {
		sendCmd("shutdown");
	}

	public OsInfo getOsInfo() throws Exception {
		return (OsInfo) getObject("obj_OS");
	}

	public JvmMemInfo getJvmMemInfo() throws Exception {
		return (JvmMemInfo) getObject("obj_JvmMemInfo");
	}

	public SysInfo getSysInfo() throws Exception {
		return (SysInfo) getObject("obj_SysInfo");
	}

	public QueueInfo[] getQueueInfos() throws Exception {
		ServersInfo serversInfo = (ServersInfo) getObject("obj_ServersInfo");
		ServerInfo[] serverInfos = serversInfo.getServerInfos();
		List<ServerInfo> queueList = new LinkedList<ServerInfo>();
		for (ServerInfo serverInfo: serverInfos)
			if (serverInfo instanceof QueueInfo)
				queueList.add(serverInfo);
		return queueList.toArray(new QueueInfo[0]);
	}
}
