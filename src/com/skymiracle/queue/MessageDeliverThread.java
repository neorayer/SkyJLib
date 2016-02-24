package com.skymiracle.queue;

public class MessageDeliverThread extends Thread {
	private QueueImpl queue;

	private QueueItem qitem;

	/**
	 * init for add message
	 * 
	 * @param queue
	 * @param message
	 */
	public MessageDeliverThread(QueueImpl queue, QueueItem qitem) {
		this.queue = queue;
		this.qitem = qitem;
		setName(this.queue.getName() + " Thread");
	}

	@Override
	public void run() {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.queue.deliver(this.qitem);
	}
}
