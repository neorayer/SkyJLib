package com.skymiracle.queue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.skymiracle.server.ServerImpl;
import com.skymiracle.server.ServerInfo;

public abstract class QueueImpl extends ServerImpl implements Queue {

	// 队列项目最大数
	private int maxLength = 10000;

	// private int messageLifetime = 0;

	// 检验周期
	private int checkInterval = 500;

	// 服务停止标志
	private boolean stopFlag = true;

	// 最大并发投递线程数
	protected int maxDeliverThreadCount = 10;

	// 当前投递线程数
	protected AtomicInteger deliverThreadCount = new AtomicInteger(0);

	// 队列列表
	protected LinkedList<QueueItem> itemList = new LinkedList<QueueItem>();

	// protected boolean isAllPermanence = false;

	protected Object lock = new Object();
	
	public QueueImpl(String name) {
		super(name);
	}

	public QueueImpl() {
		super();
	}

	public void clean() {
		// TODO Auto-generated method stub

	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setCheckInterval(int millsSeconds) {
		this.checkInterval = millsSeconds;
	}

	public int getCheckInterval() {
		return this.checkInterval;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxDeliverThreadCount(int count) {
		this.maxDeliverThreadCount = count;
	}

	public int getMaxDeliverThreadCount() {
		return this.maxDeliverThreadCount;
	}

	// public void setMessageLifetime(int seconds) {
	// this.messageLifetime = seconds;
	// }
	//
	// public int getMessageLifetime() {
	// return this.messageLifetime;
	// }

	public int getDeliverThreadCount() {
		return this.deliverThreadCount.get();
	}

	/**
	 * when messageList size is smaller then the maxDeliverThreadCount,create a
	 * delivethread to deliver the message. else store the message content ,add
	 * the message index to the messageList
	 * 
	 * @param message
	 * @throws Exception
	 */
	public void add(Object message) throws Exception {
		QueueItem qitem = new QueueItem(message);
		if (this.getLength() >= this.maxLength)
			throw new Exception(this.name + "is full. maxLength="
					+ this.maxLength);
		synchronized (this.lock) {
			this.itemList.add(qitem);
		}

	}

	public int getLength() {
		return this.itemList.size();
	}

	public void flush() {
		// TODO
	}

	public void start() {
		this.stopFlag = false;
		try {
			reload();
		} catch (IOException e) {
			warn("QueueImpl.start(), reload Exception", e);
		}
		Thread patrolThread = new Thread(this);
		patrolThread.setName(new StringBuffer("Queue Patrol Thread: ").append(
				this.name).toString());
		patrolThread.start();
		info(new StringBuffer("Queue[").append(this.name).append("] started."));
	}

	protected void reload() throws IOException {
		this.itemList.clear();
		String[] uuids = permanenceLoadAllUUIDs();
		for (String uuid : uuids) {
			QueueItem qitem = new QueueItem(uuid);
			this.itemList.add(qitem);
		}
	}

	public void run() {
		try {
			patrol();
		} catch (Exception e) {
			fatalError(
					"QueueImpl.run(), queueInfo: \r\n" + toString() + "\r\n", e);
		}
		this.stopFlag = true;
	}

	public void stop() {
		this.stopFlag = true;
	}

	/**
	 * get message ,remove the message record, create the deliver thread deliver
	 * it
	 * 
	 */
	private void patrol() {
		while (!this.getStopFlag()) {
			if (createDeliverThread())
				try {
					Thread.sleep(this.checkInterval);
					continue;
				} catch (InterruptedException e) {
					error("MessageQueueImpl.patrol ", e);
					break;
				}
		}
	}

	public boolean getStopFlag() {
		return this.stopFlag;
	}

	/**
	 * 
	 * @return need waiting
	 */
	protected boolean createDeliverThread() {
		try {
			// 队列中无项目， 返回
			if (this.itemList.size() == 0)
				return true;

			// 当前投递线程“大于等于”最大线程数
			// 缓存队列中实体项目
			if (this.deliverThreadCount.get() >= this.maxDeliverThreadCount) {
				// if (this.isAllPermanence
				// || this.deliverThreadCount >= this.maxDeliverThreadCount) {

				// 缓存所有队列选项
				for (QueueItem qitem : this.itemList) {
					if (qitem.getMessage() != null) {
						try {
							this.permanenceAdd(qitem.getUUID(), qitem
									.getMessage());
							qitem.setMessage(null);
							return false;
						} catch (IOException e) {
							error("QueueImplcreateDeliverThread() Error.", e);
						}
					}
				}

				if (this.deliverThreadCount.get() >= this.maxDeliverThreadCount)
					return true;
			}

			// QueueItem qitem = (QueueItem) this.messageList.get(0);
			// this.messageList.remove(0);
			QueueItem qitem = this.itemList.getFirst();

			if (!isTimeToDeliver(qitem))
				return true;

			try {

				// if (this.itemList.size() > 1)
				// this.itemList.removeFirst();
				// else
				// synchronized (this.lock) {
				// this.itemList.removeFirst();
				// }

				synchronized (this.lock) {
					this.itemList.removeFirst();
				}

				plusDeliverThreadCount(1);
				MessageDeliverThread mdThread = new MessageDeliverThread(this,
						qitem);
				mdThread.start();
			} catch (Exception e) {
				// this.messageList.clear();
				this.itemList = null;
				this.itemList = new LinkedList<QueueItem>();
				error("QueueImpl.createDeliverThread()", e);
				return true;
			}
		} catch (Exception e) {
			error("QueueImpl.createDeliverThread.all.exception()", e);
		}
		return false;
	}

	// You need override it if you can implement your own time checking.
	protected boolean isTimeToDeliver(QueueItem qitem) {
		return true;
	}

	public void plusDeliverThreadCount(int i) {
		deliverThreadCount.getAndAdd(i);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(this.name);
		sb.append("\talive:" + !getStopFlag());
		sb.append("\tcheckInterval:" + getCheckInterval());
		sb.append("\tlength:" + getLength() + "/" + getMaxLength());
		sb.append("\tdeliver:" + getDeliverThreadCount() + "/"
				+ getMaxDeliverThreadCount());
		return sb.toString();
	}

	public QueueInfo getQueueInfo() {
		QueueInfo queueInfo = new QueueInfo();
		queueInfo.setAlive(!getStopFlag());
		queueInfo.setLength(getLength());
		queueInfo.setMaxLength(getMaxLength());
		queueInfo.setDeliverThreadCount(getDeliverThreadCount());
		queueInfo.setMaxDeliverThreadCount(getMaxDeliverThreadCount());
		return queueInfo;
	}

	public void deliver(QueueItem qitem) {
		if (qitem.getMessage() == null) {
			try {
				qitem.setMessage(permanenceLoad(qitem.getUUID()));
			} catch (IOException e) {
				error("QueueImpl.deliver(). permanenceLoad throw exception", e);
				e.printStackTrace();
			}
			try {
				this.permanenceDel(qitem.getUUID());
			} catch (IOException e) {
				error("QueueImpl.deliver(). permanenceDel throw exception", e);
				e.printStackTrace();
			}
		}
		deliver(qitem.getUUID(), qitem.getMessage());
		plusDeliverThreadCount(-1);
	}

	public String[] getQueueItemStrs() {
		String[] qis = new String[this.itemList.size()];
		for (int i = 0; i < this.itemList.size(); i++) {
			QueueItem qitem = this.itemList.get(i);
			qis[i] = qitem.toString();
		}
		return qis;
	}

	protected abstract void deliver(String uuid, Object message);

	protected abstract void permanenceDel(String uuid) throws IOException;

	protected abstract Object permanenceLoad(String uuid) throws IOException;

	protected abstract String[] permanenceLoadAllUUIDs() throws IOException;

	protected abstract void permanenceAdd(String uuid, Object message)
			throws IOException;

	@Override
	protected ServerInfo newServerInfoInstance() {
		QueueInfo queueInfo = new QueueInfo();
		queueInfo.setAlive(!this.stopFlag);
		queueInfo.setDeliverThreadCount(this.deliverThreadCount.get());
		queueInfo.setLength(getLength());
		queueInfo.setMaxDeliverThreadCount(this.maxDeliverThreadCount);
		queueInfo.setMaxLength(this.maxLength);
		return queueInfo;
	}
}
