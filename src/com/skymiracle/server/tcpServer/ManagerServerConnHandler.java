package com.skymiracle.server.tcpServer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.server.ServerManager;
import com.skymiracle.sysinfo.OsInfo;
import com.skymiracle.sysinfo.SysInfo;
import com.skymiracle.sysinfo.UnixSysInfoGetter;

public class ManagerServerConnHandler extends TcpConnHandler<ManagerServer> {
	private static String MSG_WELCOME = "WELCOME to SkyMiracle tcp server management service.";

	private final static int CMDPOS_NEEDUSERNAME = 0;

	private final static int CMDPOS_NEEDPASSWORD = 1;

	private final static int CMDPOS_AUTHSUCC = 2;

	private final static int CMDPOS_AUTHFAILED = CMDPOS_NEEDUSERNAME;

	private int cmdPos = CMDPOS_NEEDUSERNAME;

	@Override
	public void handleConnection() throws Exception {
		lnWriter.println(MSG_WELCOME);

		String s = "";
		String r = "-ERR Invalid command";
		String username = null;
		String password = null;
		ManagerServer mgrServer = this.tcpServer;
		ServerManager serverManager = mgrServer.getManager();
		while (!s.toLowerCase().equals("quit")) {
			s = lnReader.readLine();
			r = "-ERR Invalid command";

			if (s == null)
				break;
			this.tcpServer.debug("<- " + s);
			String sHead = s.toLowerCase();
			String sTail = "";
			int pos = s.indexOf(' ');
			if (pos > 0) {
				sHead = s.substring(0, pos).toLowerCase();
				sTail = s.substring(pos + 1);
			}

			switch (this.cmdPos) {
			case CMDPOS_NEEDUSERNAME:
				if (sHead.equals("user")) {
					username = sTail;
					this.cmdPos = CMDPOS_NEEDPASSWORD;
					r = "+OK Password required.";
				}
				break;
			case CMDPOS_NEEDPASSWORD:
				if (sHead.equals("pass")) {
					password = sTail;
					if (mgrServer.auth(username, password)) {
						r = "+OK";
						this.cmdPos = CMDPOS_AUTHSUCC;
					} else {
						r = "-ERR AUTH FAILED";
						this.cmdPos = CMDPOS_AUTHFAILED;
					}
				}
				break;
			case CMDPOS_AUTHSUCC:
				if (sHead.equals("list")) {
					r = serverManager.toString() + "\r\n.";
				} else if (sHead.equals("stopserver")) {
					String serverName = sTail;
					serverManager.stopServer(serverName);
					r = "+OK";
				} else if (sHead.equals("startserver")) {
					String serverName = sTail;
					serverManager.startServer(serverName);
					r = "+OK";
				} else if (sHead.equals("getenv")) {
					Map map = System.getenv();
					Set set = map.keySet();
					Iterator it = set.iterator();
					StringBuffer sb = new StringBuffer();
					while (it.hasNext()) {
						Object key = it.next();
						sb.append((String) key).append("=").append(
								(String) map.get(key)).append("\r\n");
					}
					sb.append(".");
					r = sb.toString();
				} else if (sHead.equals("thread")) {
					StringBuffer sb = new StringBuffer();
					int c = Thread.activeCount();
					sb.append("Thread Count =").append(c).append("\r\n");
					sb.append("------------------\r\n");
					Thread[] ts = new Thread[c];
					Thread.enumerate(ts);
					for (int i = 0; i < ts.length; i++)
						sb.append(ts[i].getName()).append(" \r\n");
					r = sb.toString();
				} else if (sHead.equals("mem")) {
					StringBuffer sb = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					sb.append(
							runtime.freeMemory() + ", " + runtime.totalMemory()
									+ ", " + runtime.maxMemory())
							.append("\r\n");
					r = sb.toString();
				} else if (sHead.equals("lang-test")) {
					StringBuffer sb = new StringBuffer();
					sb.append(sTail);
					r = sb.toString();
				} else if (sHead.equals("shutdown")) {
					this.tcpServer.info("SYSTEM: The Application is shutdown.");
					System.exit(0);
				} else if (sHead.equals("gc")) {
					StringBuffer sb = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					sb.append(
							runtime.freeMemory() + ", " + runtime.totalMemory()
									+ ", " + runtime.maxMemory())
							.append("\r\n");
					System.gc();
					sb.append(
							runtime.freeMemory() + ", " + runtime.totalMemory()
									+ ", " + runtime.maxMemory())
							.append("\r\n");
					r = sb.toString();
				} else if (sHead.equalsIgnoreCase("retrfile")) {
					String logpath = sTail;
					try {
						StreamPipe.fileToOutput(logpath, this.socket
								.getOutputStream(), false);
					} catch (Exception e) {

					}
					r = ".";
				} else if (sHead.equalsIgnoreCase("obj_OS")) {
					OsInfo osInfo = UnixSysInfoGetter.getInstance().getOsInfo();
					StreamPipe.objectToOutput(osInfo, this.socket
							.getOutputStream(), true);
					return;
				} else if (sHead.equalsIgnoreCase("obj_SysInfo")) {
					SysInfo sysInfo = UnixSysInfoGetter.getInstance()
							.getSysInfo();
					StreamPipe.objectToOutput(sysInfo, this.socket
							.getOutputStream(), true);
					return;
				} else if (sHead.equalsIgnoreCase("obj_ServersInfo")) {
					Object serversInfo = this.tcpServer.getServersInfo();
					StreamPipe.objectToOutput(serversInfo, this.socket
							.getOutputStream(), true);
					return;
				} else {
					String execRes = this.tcpServer.getManager().execCmd(s);
					r = execRes == null ? r : execRes;
				}
				break;
			default:
				break;
			}

			if (sHead.equals("quit")) {
				r = "+OK bye";
			}

			lnWriter.println(r);
			this.tcpServer.debug("-> " + r);
		}
	}
}
