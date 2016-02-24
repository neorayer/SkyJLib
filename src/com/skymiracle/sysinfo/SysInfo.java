package com.skymiracle.sysinfo;

import java.io.Serializable;
import java.util.List;

public class SysInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3522154450825290912L;

	private List<DiskSpaceInfo> diskSpaceInfos;

	protected String hostname;

	protected String ipAddr;

	protected JvmMemInfo jvmMemInfo;

	protected LoadAvg loadAvg;

	protected OsInfo osInfo;

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddr() {
		return this.ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public JvmMemInfo getJvmMemInfo() {
		return this.jvmMemInfo;
	}

	public void setJvmMemInfo(JvmMemInfo jvmMemInfo) {
		this.jvmMemInfo = jvmMemInfo;
	}

	public LoadAvg getLoadAvg() {
		return this.loadAvg;
	}

	public void setLoadAvg(LoadAvg loadAvg) {
		this.loadAvg = loadAvg;
	}

	public OsInfo getOsInfo() {
		return this.osInfo;
	}

	public void setOsInfo(OsInfo osInfo) {
		this.osInfo = osInfo;
	}

	public List<DiskSpaceInfo> getDiskSpaceInfos() {
		return diskSpaceInfos;
	}

	public void setDiskSpaceInfos(List<DiskSpaceInfo> diskSpaceInfos) {
		this.diskSpaceInfos = diskSpaceInfos;
	}




}
