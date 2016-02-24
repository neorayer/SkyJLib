package com.skymiracle.server.tcpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLServerSocketFactory;

import com.skymiracle.server.ServerImpl;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.util.CalendarUtil;

/**
 * start the TcpServer,create the ServerSocket and the listening Thread.
 * 
 * @author Administrator
 * 
 */
public abstract class TcpServer extends ServerImpl {

	// 服务器端口号
	private int port;

	// TCP连接数
	private int connCount = 0;

	// 最大连接数
	private int maxConn = 0;

	// TCP连接超时时间
	/*
	 * 启用/禁用带有指定超时值的 SO_TIMEOUT，以毫秒为单位。将此选项设为非零的超时值时，在与此 Socket 关联的 InputStream
	 * 上调用 read() 将只阻塞此时间长度。如果超过超时值，将引发 java.net.SocketTimeoutException，虽然
	 * Socket 仍旧有效。
	 */
	private int cmdTimeoutSeconds = 0;

	private boolean isRunning = false;

	protected ServerSocket sSocket;

	private Class<? extends TcpConnHandler<? extends TcpServer>> connHandlerClass;

	// 服务启动时间
	private long millStartTime = 0;

	// 拒绝IP列表
	private Map<String, String> rejectIPMap;

	// 信任IP列表
	private Map<String, Long> allowIPMap;

	// 默认编码方式
	protected String defaultCharset = "ISO-8859-1";

	// 是否记录TCP连接记录
	private boolean curTcpConnStatusNeedRecord = false;
	// TCP连接情况列表
	private Hashtable<String, TcpConnStatus> curTcpConnStatusTable;

	// TCP连接频率检测周期
	private int connRateSeconds = 60;
	// TCP连接频率限制
	private boolean connRateLimited = false;
	// 最大连接频率
	private int maxConnRate = 30;
	// TCP连接频率列表
	private Hashtable<String, TcpConnCount> tcpConnRateTable;

	// 针对IP的TCP连接最大数
	private int maxIPCurConn = 0;
	// TCP连接次数列表
	private Hashtable<String, TcpConnCount> tcpCurConnCountTable;

	private ConnectionFilter connClosedFilter = new NullConnectionFilter();

	// 连接过滤列表
	private List<ConnectionFilter> connectionFilters = new LinkedList<ConnectionFilter>();

	// 处理结果后，是否关闭客户端连接
	private boolean isCloseSocketAfterHandle = true;

	// private boolean isRUDP = false;

	// private ExecutorService exe;

	public TcpServer(
			String name,
			int port,
			Class<? extends TcpConnHandler<? extends TcpServer>> connHandlerClass)
			throws Exception {
		this(connHandlerClass);
		setName(name);
		setPort(port);
	}

	public TcpServer(
			Class<? extends TcpConnHandler<? extends TcpServer>> connHandlerClass) {
		super();
		this.connHandlerClass = connHandlerClass;
		this.rejectIPMap = new HashMap<String, String>();
		this.allowIPMap = new HashMap<String, Long>();
		this.curTcpConnStatusTable = new Hashtable<String, TcpConnStatus>();
		this.tcpConnRateTable = new Hashtable<String, TcpConnCount>();
		this.tcpCurConnCountTable = new Hashtable<String, TcpConnCount>();

		// exe = Executors.newFixedThreadPool(5000);
	}

	public ConnectionFilter getConnClosedFilter() {
		return connClosedFilter;
	}

	public void setConnClosedFilter(ConnectionFilter connClosedFilter) {
		this.connClosedFilter = connClosedFilter;
	}

	public boolean isCurTcpConnStatusNeedRecord() {
		return this.curTcpConnStatusNeedRecord;
	}

	public void setCurTcpConnStatusNeedRecord(boolean curTcpConnStatusNeedRecord) {
		this.curTcpConnStatusNeedRecord = curTcpConnStatusNeedRecord;
	}

	public String getDefaultCharset() {
		return defaultCharset;
	}

	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	public boolean isCloseSocketAfterHandle() {
		return isCloseSocketAfterHandle;
	}

	public void setCloseSocketAfterHandle(boolean isCloseSocketAfterHandle) {
		this.isCloseSocketAfterHandle = isCloseSocketAfterHandle;
	}

	public int getConnRateSeconds() {
		return this.connRateSeconds;
	}

	public void setConnRateSeconds(int connRateSeconds) {
		this.connRateSeconds = connRateSeconds;
	}

	public void setMaxIPCurConn(int maxIPCurConn) {
		this.maxIPCurConn = maxIPCurConn;
	}

	public int getMaxIPCurConn() {
		return this.maxIPCurConn;
	}

	public boolean isConnRateLimited() {
		return this.connRateLimited;
	}

	public void setConnRateLimited(boolean connRateLimited) {
		this.connRateLimited = connRateLimited;
	}

	public int getMaxConnRate() {
		return this.maxConnRate;
	}

	public void setMaxConnRate(int maxConnRate) {
		this.maxConnRate = maxConnRate;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return this.port;
	}
	
	public List<ConnectionFilter> getConnectionFilters() {
		return connectionFilters;
	}

	public void setConnectionFilters(List<ConnectionFilter> connectionFilters) {
		this.connectionFilters = connectionFilters;
	}
	
	public synchronized void modConnCount(int i) {
		this.connCount += i;
	}

	public void addCurTcpConn(String id, String ip) {
		// 如果不需要记录TCP客户端连接情况， 返回
		if (!this.curTcpConnStatusNeedRecord)
			return;

		// 添加客户端连接情况(IP,时间)
		TcpConnStatus tcStatus = new TcpConnStatus(id, ip, System
				.currentTimeMillis());
		this.curTcpConnStatusTable.put(id, tcStatus);

		// 如果设置了TCP连接频率限制
		if (this.connRateLimited) {
			if (!ip.equals("127.0.0.1")) {
				TcpConnCount tcpConnCount = this.tcpConnRateTable.get(ip);
				if (tcpConnCount == null) {
					tcpConnCount = new TcpConnCount(ip);
					tcpConnCount.inc();
					this.tcpConnRateTable.put(ip, tcpConnCount);
				} else
					tcpConnCount.inc();
				if (tcpConnCount.getCount() > this.maxConnRate) {
					if (this.allowIPMap.get(ip) == null)
						addRejectIP(ip, "too much connection from ip=" + ip);
				}
			}
		}

		// 如果设置了针对IP的TCP连接最大数
		if (this.maxIPCurConn > 0) {
			TcpConnCount tcpConnCount = this.tcpCurConnCountTable.get(ip);
			if (tcpConnCount == null) {
				tcpConnCount = new TcpConnCount(ip);
				tcpConnCount.inc();
				this.tcpCurConnCountTable.put(ip, tcpConnCount);
			} else
				tcpConnCount.inc();
		}
	}

	public boolean isMoreThanMaxIpCurConn(String ip) {
		// No limit if ip in allowIPMap or 127.0.0.1
		if (ip.equals("127.0.0.1"))
			return false;
		if (this.allowIPMap.get(ip) != null)
			return false;

		TcpConnCount tcpConnCount = this.tcpCurConnCountTable.get(ip);
		if (tcpConnCount == null)
			return false;
		return tcpConnCount.getCount() > this.maxIPCurConn;
	}

	public void delCurTcpConn(String id, String ip) {
		if (!this.curTcpConnStatusNeedRecord)
			return;
		this.curTcpConnStatusTable.remove(id);
		if (this.maxIPCurConn > 0) {
			TcpConnCount tcpConnCount = this.tcpCurConnCountTable.get(ip);
			if (tcpConnCount != null) {
				tcpConnCount.dec();
				if (tcpConnCount.getCount() == 0)
					this.tcpCurConnCountTable.remove(ip);
			}
		}
	}

	public int getConnCount() {
		return this.connCount;
	}

	public TcpConnHandler<? extends TcpServer> newConnHandler()
			throws Exception {
		return this.connHandlerClass.newInstance();
	}

	public void start() {
		start(false);
	}

	/**
	 * start the ServerSocket and activate the isRunning thread
	 */
	public void start(boolean isSSL) {
		try {
			ServerSocket srvSocket = null;
			if (isSSL)
				srvSocket = SSLServerSocketFactory.getDefault()
						.createServerSocket(this.port);
			else
				srvSocket = new ServerSocket(this.port);

			start(srvSocket);
		} catch (IOException e) {
			info(new StringBuffer("Server[").append(this.name).append(
					"] started failed."), e);
		}
	}

	protected void start(ServerSocket sSocket) {
		this.sSocket = sSocket;

		this.isRunning = true;

		this.millStartTime = System.currentTimeMillis();
		Thread thread = new Thread(this);
		// 线程名
		thread.setName(new StringBuffer("TcpServer Listenning Thread:").append(
				this.name).append(" - Port:").append(this.port).toString());
		thread.start();
		info(new StringBuffer("Server[").append(this.name).append(
				"] started at port:").append(this.port).append(": ").append(
				toString()));

		if (this.connRateLimited) {
			// 连接频率检测线程
			Thread connRateCheckThread = new TcpConnRateCheckThread(this);
			connRateCheckThread.start();
		}

	}
	
	/**
	 * the Thread use this.sSocket.accept() to loop get the connect socket, and
	 * create separate thread for every socket to manage the client socket.
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		while (true) {
			TcpConnHandler<? extends TcpServer> connHandler;
			try {
				connHandler = newConnHandler();
			} catch (Exception e) {
				fatalError(new StringBuffer(
						"TcpServer: can't new Instance connHandler - ")
						.append(this.connHandlerClass.getName()));
				break;
			}
			try {
				Runnable runable = new TcpConnThread(this, this.sSocket
						.accept(), connHandler);
				// this.sSocket.accept(), connHandler)
				// exe.execute(new TcpServerConnectionThread(this,
				// this.sSocket.accept(), connHandler));
				new Thread(runable).start();

			} catch (IOException e) {
				break;
			}
		}

		try {
			this.sSocket.close();
			info(new StringBuffer("SkyMiracle TcpServer end Listening port:")
					.append(this.port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (this.isRunning) {
			this.isRunning = false;
			try {
				this.sSocket.close();
			} catch (IOException e) {
				warn(this.name, e);
			}
		}
	}
	
	public boolean getListening() {
		return this.isRunning;
	}

	/**
	 * 服务启动时间
	 * 
	 * @return
	 */
	private int getUptimeSeconds() {
		long millUptime = System.currentTimeMillis() - this.millStartTime;
		return (int) (millUptime / 1000);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.name).append(" ").append(this.port).append(" ").append(
				this.connHandlerClass.getName()).append("\r\n").append(
				" isRunning=").append(this.isRunning).append(" ").append(
				" connCount=").append(this.connCount).append(" uptimeSeconds=")
				.append(getUptimeSeconds()).append(" ").append(
						CalendarUtil.getLongFormat(this.millStartTime));
		sb.append(" cmdTimeoutSeconds=").append(getCmdTimeoutSeconds()).append(
				" ").append("maxConn=").append(getMaxConn());

		return sb.toString();
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public boolean isMoreThanMaxConn() {
		return this.maxConn == 0 ? false : this.connCount > this.maxConn;
	}

	public int getMaxConn() {
		return this.maxConn;
	}

	public void setCmdTimeoutSeconds(int seconds) {
		this.cmdTimeoutSeconds = seconds;
	}

	public int getCmdTimeoutSeconds() {
		return this.cmdTimeoutSeconds;
	}

	public void addRejectIP(String ip) {
		addRejectIP(ip, "NORMAL");
	}

	public void addRejectIP(String ip, String reason) {
		this.rejectIPMap.put(ip, reason);
		info(new StringBuffer(this.name).append(" add ").append(ip).append(
				" to rejectIP. reason=").append(reason));
	}

	public void delRejectIP(String ip) {
		if ("all".equals(ip)) {
			this.rejectIPMap.clear();
		} else
			this.rejectIPMap.remove(ip);
	}



	public Map<String, String> getRejectIPMap() {
		return this.rejectIPMap;
	}

	public void cleanupRejectIPMap() {
		this.rejectIPMap.clear();
	}

	public void cleanupAllowIPMap() {
		this.allowIPMap.clear();
	}

	public void addAllowIP(String ip) {
		this.allowIPMap.put(ip, new Long(System.currentTimeMillis()));
		info(new StringBuffer(this.name).append(" add ").append(ip).append(
				" to allowIP."));
	}

	public void delAllowIP(String ip) {
		this.allowIPMap.remove(ip);
	}

	public Map<String, Long> getAllowIPMap() {
		return this.allowIPMap;
	}

	public boolean isRejectIP(String ip) {
		if (ip.equals("127.0.0.1"))
			return false;
		if (this.rejectIPMap.get("*") != null)
			return this.allowIPMap.get(ip) == null;
		return this.rejectIPMap.get(ip) != null;
	}

	@Override
	public String execCmd(String serverCmd) {
		String[] ss = serverCmd.split(" ");
		String sHead = ss[0];
		String sTail = serverCmd.substring(sHead.length());
		sHead = sHead.trim().toLowerCase();
		if (sHead.equals("conns")) {
			return connsStr();
		} else if (sHead.equals("connrate")) {
			return connRateStr();
		} else if (sHead.equals("listrejectip")) {
			return rejectIPStr();
		} else if (sHead.equals("delrejectip")) {
			delRejectIP(sTail.trim());
			return "";
		}
		return null;
	}

	private String rejectIPStr() {
		StringBuffer sb = new StringBuffer();

		for (Iterator<String> iter = this.rejectIPMap.keySet().iterator(); iter
				.hasNext();) {
			String ip = iter.next();
			sb.append(ip).append("\r\n");
		}
		return sb.toString();

	}

	private String connRateStr() {
		StringBuffer sb = new StringBuffer();

		for (Iterator<TcpConnCount> iter = this.tcpConnRateTable.values()
				.iterator(); iter.hasNext();) {
			TcpConnCount tcpConnCount = iter.next();
			sb.append(tcpConnCount.toString()).append("\r\n");
		}
		return sb.toString();

	}

	private String connsStr() {
		if (!this.curTcpConnStatusNeedRecord)
			return "TcpConnStatusNeedRecord is closed by config";

		StringBuffer sb = new StringBuffer();

		long now = System.currentTimeMillis();
		for (Iterator<TcpConnStatus> iter = this.curTcpConnStatusTable.values()
				.iterator(); iter.hasNext();) {
			TcpConnStatus tcpConnStatus = iter.next();
			sb.append(tcpConnStatus.getId()).append("\t").append(
					tcpConnStatus.getRemoteIP()).append("\t").append(
					CalendarUtil
							.getLongFormat(tcpConnStatus.getConnTimestamp()))
					.append("\t")
					.append(now - tcpConnStatus.getConnTimestamp()).append(
							"\r\n");
		}
		sb.append("------------------------------\r\n");
		for (Iterator<TcpConnCount> iter = this.tcpCurConnCountTable.values()
				.iterator(); iter.hasNext();) {
			TcpConnCount tcpConnCount = iter.next();
			sb.append(tcpConnCount.getIp()).append("\t").append(
					tcpConnCount.getCount()).append("\r\n");
		}

		return sb.toString();
	}

	public void cleanConnRateTable() {
		info(this.name + " clean connRateTable");
		this.tcpConnRateTable.clear();
	}

	public String getRejectIPReason(String ip) {
		return this.rejectIPMap.get(ip);
	}

	@Override
	public ServerInfo getServerInfo() {

		// System.out.println(super.getServerInfo().getServerName());
		TcpServerInfo serverInfo = (TcpServerInfo) super.getServerInfo();
		serverInfo.setConnCount(this.connCount);
		serverInfo.setListening(this.isRunning);
		serverInfo.setMillStartTime(this.millStartTime);
		return serverInfo;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

}
