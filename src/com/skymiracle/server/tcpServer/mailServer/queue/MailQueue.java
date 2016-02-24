package com.skymiracle.server.tcpServer.mailServer.queue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.skymiracle.client.tcpClient.smtpClient.SmtpClient;
import com.skymiracle.io.Dir;
import com.skymiracle.logger.Logger;
import com.skymiracle.queue.Queue;
import com.skymiracle.queue.QueueImpl;
import com.skymiracle.util.CalendarUtil;

public abstract class MailQueue extends QueueImpl implements Queue {

	public static final String NAME_NATIVE_LOCAL = "NativeLocalQueue";

	public static final String NAME_NATIVE_REMOTE = "NativeRemoteQueue";

	public static final String NAME_FOREIGN = "ForeignQueue";

	public static final String NAME_BOUNCE_TO_NATIVE = "BounceToNativeQueue";

	public static final String NAME_BOUNCE_TO_FOREIGN = "BounceToForeignQueue";

	public static final String NAME_TIMER = "TimerQueue";
	
	public static final String NAME_FAX = "FaxQueue";

	public static final String NAME_FOREIGN_DELAY_PRE = "ForeignDelay_";

	// 邮件队列存储路径
	protected String queuePath;

	// 邮件队列服务管理
	protected MailQueueManager mailQueueManager;

	// 邮件投递
	protected SmtpClient smtpClient = new SmtpClient();

	// 
	protected MailLogger mailLogger = new MailLogger() {
		public void save(String sendTime, String mailType, String mailFrom,
				String rcptTo, long size, String result, String cause,
				String remark) {
			// TODO Auto-generated method stub
		}
	};

	public MailQueue() throws Exception {
		this("NoNameMailQueue");
	}

	public MailQueue(String queueName) throws Exception {
		this(queueName, "/wpx/queue/");
	}
	
	public MailQueue(String queueName, String queuePath) throws Exception {
		super(queueName);
		this.queuePath = queuePath;
	}

	public void setSmtpClient(SmtpClient smtpClient) {
		this.smtpClient = smtpClient;
	}

	public String getQueuePath() {
		return this.queuePath;
	}

	public void setQueuePath(String queuePath) throws Exception {
		this.queuePath = queuePath;
		Dir queueDir = new Dir(this.queuePath);
		if (!queueDir.exists()) {
			if (!queueDir.mkdirs())
				throw new Exception("Failed to create queueDir "
						+ this.queuePath);
		}
	}

	public void setMailLogger(MailLogger mailLogger) {
		this.mailLogger = mailLogger;
	}

	public MailLogger getMailLogger() {
		return mailLogger;
	}

	public MailQueueManager getMailQueueManager() {
		return this.mailQueueManager;
	}

	public void setMailQueueManager(MailQueueManager mailQueueManager) {
		this.mailQueueManager = mailQueueManager;
	}

	@Override
	protected Object permanenceLoad(String uuid) {
		String dataFilePath = new StringBuffer(this.queuePath).append("/")
				.append(uuid).toString();
		try {
			return new MailMessage(dataFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void permanenceAdd(String uuid, Object message)
			throws IOException {
		MailMessage mm = (MailMessage) message;
		mm.save(this.queuePath + "/" + uuid);
	}

	@Override
	protected void permanenceDel(String uuid) throws IOException {
		String dataFilePath = new StringBuffer(this.queuePath).append("/")
				.append(uuid).toString();
		MailMessage.remove(dataFilePath);
	}

	@Override
	protected String[] permanenceLoadAllUUIDs() throws IOException {
		Dir queueDir = new Dir(this.queuePath);
		File[] files = queueDir.listFiles();
		List<String> uuidList = new ArrayList<String>();
		
		for(File file: files) {
			String name = file.getName();
			if (!name.endsWith(".props"))
				continue;
			String uuid = name.substring(0, name.length() - 6);
			uuidList.add(uuid);
			
		}
		
		return uuidList.toArray(new String[0]);
	}

	@Override
	public String execCmd(String serverCmd) {
		String[] ss = serverCmd.split(" ");
		String sHead = ss[0];
		// String sTail = serverCmd.substring(sHead.length());
		sHead = sHead.trim().toLowerCase();
		if (sHead.equals("list")) {
			return list();
		}
		return null;
	}

	public String list() {
		StringBuffer sb = new StringBuffer();
		String[] queueItemStrs = super.getQueueItemStrs();
		for (int i = 0; i < queueItemStrs.length; i++)
			sb.append(queueItemStrs[i]).append("\r\n");
		return sb.toString();
	}
	
	protected void saveLog(MailMessage mm, String mailType, String result,
			String cause) {
		String shortTo = mm.getToStr();
		if (shortTo.length() > 60)
			shortTo = shortTo.substring(0, 60) + "...";
		String logmsg = String.format("%s :%s (%s) to=<%s>", this.name, result,
				cause, shortTo);
		Logger.info(logmsg);
		
		/*
		 * sendTime;mailType;mailFrom;rcptTo;size;result;cause;remark;
		 */
		String sendTime = CalendarUtil.getLocalDateTime("yyyy-MM-dd HH:mm:ss");
		String mailFrom = mm.getFromUsername() + "@" + mm.getFromDomain();
		String rcptTo = shortTo;
		long size = mm.getSize();
		String remark = mm.getFromDomain();

		mailLogger.save(sendTime, mailType, mailFrom, rcptTo, size, result,
				cause, remark);

	}

}
