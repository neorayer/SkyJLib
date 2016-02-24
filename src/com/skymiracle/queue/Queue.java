package com.skymiracle.queue;

import com.skymiracle.server.Server;

public interface Queue extends Server {

	// public final static int EXTREME_MAX_LENGTH = 10000;

	/**
	 * Flush the message queue.
	 * 
	 */
	public void flush();

	/**
	 * Clean the message queue.
	 * 
	 */
	public void clean();

	/**
	 * Set the max length limit of the message queue.
	 * 
	 * @param maxLength
	 *            the max length of message queue.
	 */
	public void setMaxLength(int maxLength);

	/**
	 * Get the max length limit of the message queue.
	 * 
	 * @return the max message queue length.
	 */
	public int getMaxLength();

	/**
	 * Set the queue checking interval.
	 * 
	 * @param millsSeconds
	 *            the mills seconds of checking interval.
	 */
	public void setCheckInterval(int millsSeconds);

	/**
	 * Get the queue checking interval.
	 * 
	 * @return the mill seconds of checking interval.
	 */
	public int getCheckInterval();

	/**
	 * Set the max count of the deliver thread simultaneously.
	 * 
	 * @param the
	 *            max thread count.
	 */
	public void setMaxDeliverThreadCount(int count);

	/**
	 * Get the max count of the deliver thread simultaneously.
	 * 
	 * @return the max thread count.
	 */
	public int getMaxDeliverThreadCount();

	/**
	 * Set the message life time seconds.
	 * 
	 * @param seconds
	 *            the message life time seconds.
	 */
//	public void setMessageLifetime(int seconds);

	/**
	 * Get the message lifetime seconds.
	 * 
	 * @return the seconds of message lifetime.
	 */
//	public int getMessageLifetime();

	/**
	 * Add a String type message to the message queue.
	 * 
	 * @throws Exception
	 * 
	 */
	public void add(Object message) throws Exception;

	/**
	 * Get the messages the message queue length.
	 * 
	 * @return the messages length.
	 */
	public int getLength();

	public void plusDeliverThreadCount(int i);

	public int getDeliverThreadCount();

	public String[] getQueueItemStrs();

	public QueueInfo getQueueInfo();
}
