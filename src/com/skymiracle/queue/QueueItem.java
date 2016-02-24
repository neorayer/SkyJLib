package com.skymiracle.queue;

import com.skymiracle.util.UUID;

public class QueueItem {

	private Object message;

	private String uuid;

	private String info;

	private long genTime;

	public QueueItem(String uuid) {
		this.message = null;
		this.info = "NULL";
		this.uuid = uuid;
		this.genTime = System.currentTimeMillis();
	}

	public QueueItem(Object message) {
		this.message = message;
		this.info = this.message == null ? "NULL" : this.message.toString();
		this.uuid = new UUID().toShortString();
		this.genTime = System.currentTimeMillis();
	}

	public String getUUID() {
		return this.uuid;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Object getMessage() {
		return this.message;
	}

	@Override
	public String toString() {
		return this.uuid + "\t" + this.info;
	}

	public long getGenTime() {
		return this.genTime;
	}
}