package com.skymiracle.sysinfo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.logger.Logger;
import com.skymiracle.system.ShellCommand;

public class UnixSysInfoGetter extends SysInfoGetterAbs implements
		SysInfoGetter {

	private static SysInfoGetter sysInfo = null;

	private UnixSysInfoGetter() {

	}

	public static SysInfoGetter getInstance() {
		if (sysInfo == null)
			sysInfo = new UnixSysInfoGetter();
		return sysInfo;
	}

	@Override
	public void makeHostname() {
		String cmd = "hostname";
		String hostname = shCmdRes(cmd);
		this.hostname = hostname;
	}

	@Override
	public void makeIpAddr() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			this.ipAddr = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			Logger.debug("UnixSysInfo.getIpAddr(), ", e);
			this.ipAddr = "_CAN NOT GET IPADDR";
		}
	}

	@Override
	protected void makeOsInfo() {
		this.osInfo = new OsInfo();
		this.osInfo.setMachineHardwareName(shCmdRes("uname -m"));
		this.osInfo.setNodename(shCmdRes("uname -n"));
		this.osInfo.setOsName(shCmdRes("uname -s"));
		this.osInfo.setOsRelease(shCmdRes("uname -r"));
		this.osInfo.setOsVersion(shCmdRes("uname -v"));
		this.osInfo.setProcessorType(shCmdRes("uname -p"));
	}

	private String shCmdRes(String cmd) {
		try {
			return ShellCommand.exec(cmd, "iso8859-1").trim();
		} catch (IOException e) {
			Logger.debug("UnixSysInfo.shCmdRes(), ", e);
		}
		return "Cmd error " + cmd;
	}

	public List<DiskSpaceInfo> getDiskSpaceInfos() {
		List<DiskSpaceInfo> infos = new LinkedList<DiskSpaceInfo>();
		File[] roots = File.listRoots();
		for (File _file : roots) {
			DiskSpaceInfo info = new DiskSpaceInfo();
			info.setFileSystem(_file.getAbsolutePath());
//			info.setFreeSpace(_file.getFreeSpace());
//			info.setUsableSpace(_file.getUsableSpace());
//			info.setTotalSpace(_file.getTotalSpace());
			infos.add(info);
		}
		return infos;
	}

	public LoadAvg getLoadAvg() {
		LoadAvg la = new LoadAvg();
		String str = shCmdRes("uptime");
		String[] ss = str.split(" ");
		String exp1Str = ss[ss.length - 3];
		String exp5Str = ss[ss.length - 2];
		String exp15Str = ss[ss.length - 1];

		if (exp1Str.endsWith(","))
			exp1Str = exp1Str.substring(0, exp1Str.length() - 2);
		if (exp5Str.endsWith(","))
			exp5Str = exp1Str.substring(0, exp5Str.length() - 2);
		if (exp15Str.endsWith(","))
			exp15Str = exp1Str.substring(0, exp15Str.length() - 2);
		la.setExp1(Float.parseFloat(exp1Str));
		la.setExp5(Float.parseFloat(exp5Str));
		la.setExp15(Float.parseFloat(exp15Str));
		return la;
	}

	public SysInfo getSysInfo() {
		SysInfo sysInfo = new SysInfo();
		sysInfo.setDiskSpaceInfos(getDiskSpaceInfos());
		sysInfo.setHostname(getHostname());
		sysInfo.setIpAddr(getIpAddr());
		sysInfo.setJvmMemInfo(getJvmMemInfo());
		sysInfo.setLoadAvg(getLoadAvg());
		sysInfo.setOsInfo(getOsInfo());
		return sysInfo;
	}

	public static void main(String[] args) {
		Logger.info("begin");
		SysInfoGetter siGetter = UnixSysInfoGetter.getInstance();
		System.out.println(siGetter.getSysInfo());
		Logger.info("end");

	}
}
