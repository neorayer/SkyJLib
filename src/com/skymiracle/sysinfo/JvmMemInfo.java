package com.skymiracle.sysinfo;

import java.io.Serializable;

public class JvmMemInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8451620856636476003L;

	private long free;

	private long total;

	private long max;

	public long getFree() {
		return this.free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public long getMax() {
		return this.max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getTotal() {
		return this.total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUsed() {
		return this.total - this.free;
	}

	public StringBuffer toStringBuffer() {
		StringBuffer sb = new StringBuffer();
		sb.append("Free\tTotal\tMax\tUsed\r\n");
		sb.append(getFree());
		sb.append("\t");
		sb.append(getTotal());
		sb.append("\t");
		sb.append(getMax());
		sb.append("\t");
		sb.append(getUsed());
		sb.append("\r\n");

		return sb;
	}

	@Override
	public String toString() {
		return toStringBuffer().toString();
	}
}
