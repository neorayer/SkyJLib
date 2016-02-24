package com.skymiracle.server.tcpServer.mailServer.queue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import com.skymiracle.io.Dir;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.Server;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.util.UUID;

public class SimpleTimerMailQueue extends Thread implements Server {
	private LinkedList timerList = new LinkedList();

	protected int maxLength = 30000;

	private int messageLifetime = 0;

	protected int checkInterval = 60000;

	private boolean running = false;

	private boolean stopFlag = true;

	private String queuePath = "";

	public SimpleTimerMailQueue(String queuePath) {
		super(MailQueue.NAME_TIMER);
		this.queuePath = queuePath;
		Dir dir = new Dir(this.queuePath);
		if (!dir.exists())
			dir.mkdirs();
		else
			init(dir);
	}

	private MailQueueManager mailQueueManager;

	public void setMailQueueManager(MailQueueManager mailQueueManager) {
		this.mailQueueManager = mailQueueManager;
	}

	private void init(Dir dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().indexOf(".props") != -1)
				continue;
			this.add(new TimeQueueItem(files[i].getAbsolutePath()));
		}
	}

	public int getCheckInterval() {
		return this.checkInterval;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getMessageLifetime() {
		return this.messageLifetime;
	}

	public boolean isRunning() {
		return this.running;
	}

	public boolean isStopFlag() {
		return this.stopFlag;
	}

	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setMessageLifetime(int messageLifetime) {
		this.messageLifetime = messageLifetime;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}

	public void patrol() {
		while (this.stopFlag) {
			try {
				clockRingDeliver();
				Thread.sleep(this.checkInterval);
			} catch (InterruptedException e) {
			}
		}
	}

	private void clockRingDeliver() {
		Iterator it = this.timerList.listIterator();
		long nowTimeStamp = getNowTimeStamp();
		while (it.hasNext()) {
			TimeQueueItem t = (TimeQueueItem) it.next();
			if (clockRing(nowTimeStamp, t.getBeginTimeStamp())) {
				try {
					this.mailQueueManager.putMailtoQueue(t.getFromusername(), t
							.getFromdomain(), t.getToStrs(),
							this.permanenceLoad(t.getUUID(), t
									.getBeginTimeStamp()), new UUID()
									.toShortString());
				} catch (Exception e) {
				}
				this.permanenceDel(t);
				it.remove();
			}
		}
	}

	private boolean clockRing(long now, long timer) {
		if (now - timer > 0)
			return true;
		return false;
	}

	private long getNowTimeStamp() {
		return System.currentTimeMillis();
	}

	public boolean add(MailMessage message, long beginTime) {
		if (this.timerList.size() >= this.maxLength)
			return false;
		TimeQueueItem tqi = permanenceAdd(new UUID().toShortString(), message,
				beginTime);
		this.timerList.add(tqi);
		return true;
	}

	public boolean add(TimeQueueItem t) {
		if (this.timerList.size() >= this.maxLength)
			return false;
		this.timerList.add(t);
		return true;
	}

	public void run() {
		this.running = true;
		Logger.info(new StringBuffer("Queue[").append(MailQueue.NAME_TIMER)
				.append("] started."));
		patrol();
	}

	protected TimeQueueItem permanenceAdd(String uuid, MailMessage message,
			long beginTime) {
		TimeQueueItem t = new TimeQueueItem(uuid, message, beginTime,
				this.queuePath);
		return t;
	}

	protected void permanenceDel(TimeQueueItem t) {
		try {
			t.remove(this.queuePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected ArrayList permanenceLoad(String uuid, long beginTime) {
		try {
			return TextFile.loadLinesList(this.queuePath + "/" + uuid + "_"
					+ beginTime);
		} catch (IOException e) {
			return null;
		}

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(MailQueue.NAME_TIMER);
		sb.append("\talive:" + this.stopFlag);
		sb.append("\tcheckInterval:" + getCheckInterval());
		sb.append("\tlength:" + this.timerList.size() + "/" + this.maxLength);
		return sb.toString();
	}

	public static void main(String[] args) {
		SimpleTimerMailQueue stm = new SimpleTimerMailQueue(
				"/tmp/queue/timer.queue/");
		stm.start();
		ArrayList dataLineList = new ArrayList();
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		dataLineList.add("1111111111111111111111111");
		MailMessage m = new MailMessage("disller", "test.com",
				"disller@test.com,", "local", dataLineList, new UUID()
						.toShortString());
		stm.add(m, getTimeStamp("3", "9", "20", "26"));
		stm.add(m, getTimeStamp("3", "9", "20", "35"));
		stm.add(m, getTimeStamp("3", "9", "20", "34"));
		stm.add(m, getTimeStamp("3", "9", "20", "26"));
		stm.add(m, getTimeStamp("3", "9", "20", "34"));
		stm.add(m, getTimeStamp("3", "9", "20", "24"));
		stm.add(m, getTimeStamp("3", "9", "20", "19"));
		stm.add(m, getTimeStamp("3", "9", "20", "20"));
		stm.add(m, getTimeStamp("3", "9", "20", "43"));
		stm.add(m, getTimeStamp("3", "9", "20", "35"));
		stm.add(m, getTimeStamp("3", "9", "20", "35"));
		stm.add(m, getTimeStamp("3", "9", "20", "56"));
		stm.add(m, getTimeStamp("3", "9", "20", "45"));
		stm.add(m, getTimeStamp("3", "9", "20", "23"));
		stm.add(m, getTimeStamp("3", "9", "20", "54"));
		stm.add(m, getTimeStamp("3", "9", "20", "43"));
		stm.add(m, getTimeStamp("3", "9", "20", "60"));
		stm.add(m, getTimeStamp("3", "9", "20", "60"));
		System.out.println(System.currentTimeMillis());
	}

	private static long getTimeStamp(String mon, String day, String hour,
			String min) {
		Calendar c = Calendar.getInstance();
		c.set(2006, Integer.parseInt(mon) - 1, Integer.parseInt(day), Integer
				.parseInt(hour), Integer.parseInt(min));
		return c.getTimeInMillis();
	}

	public String execCmd(String serverCmd) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDbLogRun(boolean isRun) {
		// TODO Auto-generated method stub

	}

	public boolean isDbLogRun() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAppLogClass(Class appLogClass) {
		// TODO Auto-generated method stub

	}

	public void setDbLogClass(Class dbLogClass) {
		// TODO Auto-generated method stub

	}

	public ServerInfo getServerInfo() {
		return new ServerInfo();
	}

}
