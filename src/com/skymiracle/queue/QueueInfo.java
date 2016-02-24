package com.skymiracle.queue;

import com.skymiracle.server.ServerInfo;

public class QueueInfo extends ServerInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2218011519196087510L;

	private boolean alive;

	private int length;

	private int maxLength;

	private int deliverThreadCount;

	private int maxDeliverThreadCount;

	public boolean getAlive() {
		return this.alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public int getDeliverThreadCount() {
		return this.deliverThreadCount;
	}

	public void setDeliverThreadCount(int deliverThreadCount) {
		this.deliverThreadCount = deliverThreadCount;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getMaxDeliverThreadCount() {
		return this.maxDeliverThreadCount;
	}

	public void setMaxDeliverThreadCount(int maxDeliverThreadCount) {
		this.maxDeliverThreadCount = maxDeliverThreadCount;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
