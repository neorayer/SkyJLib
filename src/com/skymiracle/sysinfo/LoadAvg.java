package com.skymiracle.sysinfo;

import java.io.Serializable;

public class LoadAvg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4596834686938258703L;

	private float exp1;

	private float exp5;

	private float exp15;

	public float getExp1() {
		return this.exp1;
	}

	public void setExp1(float exp1) {
		this.exp1 = exp1;
	}

	public float getExp15() {
		return this.exp15;
	}

	public void setExp15(float exp15) {
		this.exp15 = exp15;
	}

	public float getExp5() {
		return this.exp5;
	}

	public void setExp5(float exp5) {
		this.exp5 = exp5;
	}

	public StringBuffer toStringBuffer() {
		StringBuffer sb = new StringBuffer();
		sb.append("Exp1\tExp5\tExp15\r\n");
		sb.append(getExp1());
		sb.append("\t");
		sb.append(getExp5());
		sb.append("\t");
		sb.append(getExp15());
		sb.append("\r\n");
		return sb;
	}

	@Override
	public String toString() {
		return toStringBuffer().toString();
	}
}
