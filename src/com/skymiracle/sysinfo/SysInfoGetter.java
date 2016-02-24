package com.skymiracle.sysinfo;

import java.util.List;

public interface SysInfoGetter {

	public String getHostname();

	public String getIpAddr();

	public OsInfo getOsInfo();

	public JvmMemInfo getJvmMemInfo();

	public List<DiskSpaceInfo> getDiskSpaceInfos();

	public LoadAvg getLoadAvg();

	public SysInfo getSysInfo();

}
