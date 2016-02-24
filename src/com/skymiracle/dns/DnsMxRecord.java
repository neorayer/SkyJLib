package com.skymiracle.dns;

public class DnsMxRecord {

	// 优先级
	private int pri = 0;

	// mx服务器主机名
	private String name;

	public DnsMxRecord(int pri, String name) {
		this(name);
		this.pri = pri;
	}

	public DnsMxRecord(String name) {
		this.name = name;
	}

	public int getPri() {
		return this.pri;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return new StringBuffer().append(this.pri).append(" ")
				.append(this.name).toString();
	}
}
