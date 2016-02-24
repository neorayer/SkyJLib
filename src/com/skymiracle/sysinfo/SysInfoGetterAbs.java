package com.skymiracle.sysinfo;

public abstract class SysInfoGetterAbs implements SysInfoGetter {

	protected String hostname;

	protected String ipAddr;

	protected OsInfo osInfo;

	public String getHostname() {
		if (this.hostname == null)
			makeHostname();
		return this.hostname;
	}

	protected abstract void makeHostname();

	public String getIpAddr() {
		if (this.ipAddr == null)
			makeIpAddr();
		return this.ipAddr;
	}

	protected abstract void makeIpAddr();

	public OsInfo getOsInfo() {
		if (this.osInfo == null)
			makeOsInfo();
		return this.osInfo;
	}

	protected abstract void makeOsInfo();

	public JvmMemInfo getJvmMemInfo() {
		JvmMemInfo jvmMemInfo = new JvmMemInfo();
		Runtime runtime = Runtime.getRuntime();

		jvmMemInfo.setFree(runtime.freeMemory());

		jvmMemInfo.setTotal(runtime.totalMemory());

		runtime.freeMemory();
		jvmMemInfo.setMax(runtime.maxMemory());
		return jvmMemInfo;
	}
}
