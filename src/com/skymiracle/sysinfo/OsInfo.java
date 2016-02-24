package com.skymiracle.sysinfo;

import java.io.Serializable;

public class OsInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3640857523660680641L;

	private String machineHardwareName;

	private String nodename;

	private String processorType;

	private String osName;

	private String osRelease;

	private String osVersion;

	public String getMachineHardwareName() {
		return this.machineHardwareName;
	}

	public void setMachineHardwareName(String machineHardwareName) {
		this.machineHardwareName = machineHardwareName;
	}

	public String getNodename() {
		return this.nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public String getOsName() {
		return this.osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsRelease() {
		return this.osRelease;
	}

	public void setOsRelease(String osRelease) {
		this.osRelease = osRelease;
	}

	public String getProcessorType() {
		return this.processorType;
	}

	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}

	public String getOsVersion() {
		return this.osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public StringBuffer toStringBuffer() {
		StringBuffer sb = new StringBuffer();
		sb.append(getMachineHardwareName());
		sb.append("\r\n");
		sb.append(getNodename());
		sb.append("\r\n");
		sb.append(getOsName());
		sb.append("\r\n");
		sb.append(getOsRelease());
		sb.append("\r\n");
		sb.append(getOsVersion());
		sb.append("\r\n");
		sb.append(getProcessorType());
		sb.append("\r\n");
		return sb;
	}

	@Override
	public String toString() {
		return toStringBuffer().toString();
	}

}
