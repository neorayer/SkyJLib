package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skymiracle.auth.MailUser;
import com.skymiracle.dns.RBLChecker;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.ConnectionFilter;
import com.skymiracle.server.tcpServer.TcpConnHandler;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.mailServer.AutoMailMaker;
import com.skymiracle.server.tcpServer.mailServer.MailServer;
import com.skymiracle.server.tcpServer.mailServer.queue.MailQueueManager;
import com.skymiracle.util.IpFilter;

public class SmtpServer extends MailServer {

	private String heloHostname = "defaultHeloHostName";

	// 白名单IP列表
	private Map<String, Boolean> smtpTrustIPMap = new HashMap<String, Boolean>();

	// 白名单子网段列表
	private Map<String, Boolean> smtpTrustIpCnetMap = new HashMap<String, Boolean>();

	// 是否需要权限验证
	private boolean forceAuth = false;

	// 命令过滤器
	private List<SmtpCommanderFilter> mailCmdFilters = new LinkedList<SmtpCommanderFilter>();

	//默认最大邮件发送大小
	private long maxMessageSize;

	// 是否需要垃圾邮件检查
	private boolean rblCheck = false;

	// 垃圾邮件检查器
	private RBLChecker rblChecker;

	// 邮件队列服务管理
	private MailQueueManager mailQueueManager;

	// 地址簿过滤器
	private AddrFilter addrBookFilter;

	// 
	private SmtpLogger smtpLogger = new SmtpLogger() {
		public void addFromNativeMission(String missionUUID,
				MailUser fromMailUser, List<MailUser> rcptToList, long dataSize,
				List<String> dataLineList) {
		}
	};

	private DataFilter dataFilter = new DataFilter() {
		public String doFilter(CmdConnHandler connHandler,
				SmtpHandleStatus shStatus, List<String> dataLineList) {
			return null;
		}
	};

	public SmtpServer() throws Exception {
		super("SmtpServer", 25);

		setWelcome("220 ESMTP SkyMiracle WorldPost X ESMTP Server");
		setUnknown("500 Syntax error, command unrecognized");
		setShortConn(false);

		addCommander(HeloCommander.class);
		addCommander(EhloCommander.class);
		addCommander(AuthCommander.class);
		addCommander(MailCommander.class);
		addCommander(RcptCommander.class);
		addCommander(DataCommander.class);
		addCommander(RsetCommander.class);
		addCommander(QuitCommander.class);
		addCommander(VrfyCommander.class);
		addCommander(ExpnCommander.class);
		addCommander(HelpCommander.class);
		addCommander(NoopCommander.class);

		//AntiCracker Commanders
		addCommander(AC_ListCommander.class);
		addCommander(AC_DeleCommander.class);

		
		addSmtpTrustIP("127.0.0.1");
	}

	@Override
	public void start() {
		// 启动服务器
		super.start();

		// 启动所有邮件队列
		this.mailQueueManager.startAllServers();
	}

	public SmtpLogger getSmtpLogger() {
		return smtpLogger;
	}

	public void setSmtpLogger(SmtpLogger smtpLogger) {
		this.smtpLogger = smtpLogger;
	}

	public boolean isRblCheck() {
		return rblCheck;
	}

	public void setRblCheck(boolean rblCheck) {
		this.rblCheck = rblCheck;
	}

	public DataFilter getDataFilter() {
		return dataFilter;
	}

	public void setDataFilter(DataFilter dataFilter) {
		this.dataFilter = dataFilter;
	}

	public AddrFilter getAddrBookFilter() {
		return addrBookFilter;
	}

	public void setAddrBookFilter(AddrFilter addrBookFilter) {
		this.addrBookFilter = addrBookFilter;
	}

	public MailQueueManager getMailQueueManager() {
		return mailQueueManager;
	}

	public void setMailQueueManager(MailQueueManager mailQueueManager) {
		this.mailQueueManager = mailQueueManager;
		this.mailQueueManager.setSmtpServer(this);
	}

	public void addSmtpTrustIP(String ip) {
		smtpTrustIPMap.put(ip, new Boolean(true));
	}

	public List<SmtpCommanderFilter> getMailCmdFilters() {
		return mailCmdFilters;
	}

	public boolean getForceAuth() {
		return forceAuth;
	}

	public void setForceAuth(boolean forceAuth) {
		this.forceAuth = forceAuth;
	}

	public void setMailCmdFilters(List<SmtpCommanderFilter> mailCmdFilters) {
		this.mailCmdFilters = mailCmdFilters;
	}

	public Map<String, Boolean> getSmtpTrustIPMap() {
		return smtpTrustIPMap;
	}

	public void setSmtpTrustIPMap(Map<String, Boolean> smtpTrustIPMap) {
		this.smtpTrustIPMap = smtpTrustIPMap;
	}

	public void cleanupSmtpTrustIP() {
		if (this.smtpTrustIPMap != null)
			this.smtpTrustIPMap.clear();
	}

	public Map<String, Boolean> getSmtpTrustIpCnetMap() {
		return smtpTrustIpCnetMap;
	}

	public void setSmtpTrustIpCnetMap(Map<String, Boolean> smtpTrustIpCnetMap) {
		this.smtpTrustIpCnetMap = smtpTrustIpCnetMap;
	}

	public void cleanupSmtpTrustIpCnetMap() {
		if (this.smtpTrustIpCnetMap != null)
			this.smtpTrustIpCnetMap.clear();
	}

	public boolean isSmtpTrustIP(String remoteIP) {
		for (String key : smtpTrustIPMap.keySet()) {
			// IpFilter ipFilter = new IpFilter();
			if (IpFilter.ipFilterPart(remoteIP, key))
				return true;
		}

		return false;

	}

	public boolean isSmtpTrustIpCnet(String remoteIP) {
		int pos = remoteIP.lastIndexOf('.');
		String remoteIpCnet = remoteIP.substring(0, pos);
		Boolean value = this.smtpTrustIpCnetMap.get(remoteIpCnet);
		if (value == null)
			return false;
		return value.booleanValue();
	}

	public RBLChecker getRblChecker() {
		return rblChecker;
	}

	public void setRblChecker(RBLChecker rblChecker) {
		this.rblChecker = rblChecker;
	}

	public String getHeloHostname() {
		return heloHostname;
	}

	public void setHeloHostname(String heloHostname) {
		this.heloHostname = heloHostname;
	}

	public boolean hasNativeSmtpPermission(MailUser<?> mailUser) {
		return getAuthMail().hasPermissionSmtp(mailUser);
	}

	@Override
	public MailUser<?> getMailUser(String username, String domain)
			throws Exception {
		MailUser<?> mailUser = super.getMailUser(username, domain);
		if (mailUser.getMessageSize() <= 0)
			mailUser.setMessageSize(this.maxMessageSize);
		return mailUser;
	}

	public long getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(long maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public boolean isMailBoxFull(MailUser<?> toMailUser) throws Exception {
		long storageSizeUsed = getStorageMailAccessorFactory()
				.getUserStorageMailAccessor(toMailUser)
				.mailGetStorageSizeUsed();
		return storageSizeUsed >= toMailUser.getSize();
	}

	public void putMailtoQueue(MailUser fromMailUser,
			List<MailUser> rcptToList, List<String> dataLineList,
			String missionUUID) throws InstantiationException,
			IllegalAccessException {

		// 验证received循环次数，这个方法似乎没起作用 ？？？
		if (!AutoMailMaker.receivedMailLoopCheck(dataLineList)) {
			Logger.warn("Message " + missionUUID + " is LOOP");
			return;
		}
		this.mailQueueManager.putMailtoQueue(fromMailUser.getUid(),
				fromMailUser.getDc(), rcptToList, dataLineList, missionUUID);
	}

	// ----- SmtpConnFilter

	public class SmtpConnFilter implements ConnectionFilter {

		private SmtpServer smtpServer;

		public SmtpConnFilter(SmtpServer smtpServer) {
			this.smtpServer = smtpServer;
		}

		public boolean doFilter(TcpConnHandler connHandler) {
			SmtpHandleStatus shStatus = (SmtpHandleStatus) connHandler
					.getHandleStatus();
			shStatus.setSmtpTrustIP(smtpServer.isSmtpTrustIP(connHandler
					.getRemoteIP()));
			return true;
		}
	}

}
