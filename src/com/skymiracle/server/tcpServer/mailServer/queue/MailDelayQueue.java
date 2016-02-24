package com.skymiracle.server.tcpServer.mailServer.queue;

import com.skymiracle.queue.Queue;
import com.skymiracle.queue.QueueItem;

/**
 *	定时发送队列
 */

public abstract class MailDelayQueue extends MailQueue implements Queue,
		Runnable {

	protected int level = 0;
	private long delay = 0L;

	public MailDelayQueue(int level, long delay, String queuePath,
			String queueName) throws Exception {
		super(queueName, queuePath);
		this.level = level;
		this.delay = delay * 1000;
	}

	@Override
	protected boolean isTimeToDeliver(QueueItem qitem) {
		QueueItem q = qitem;
		if (q.getGenTime() + this.delay < System.currentTimeMillis())
			return true;
		else
			return false;
	}

	@Override
	protected abstract void deliver(String uuid, Object message);

	@Override
	public String toString() {
		String pre = super.toString();
		StringBuffer sb = new StringBuffer(pre.length() + 20);
		sb.append(pre).append("\t level:").append(this.level)
				.append("\tdelay:").append(this.delay / 1000).append(" s");
		return sb.toString();
	}

}
