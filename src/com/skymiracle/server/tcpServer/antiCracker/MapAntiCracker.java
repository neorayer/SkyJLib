package com.skymiracle.server.tcpServer.antiCracker;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.TcpConnHandler;
import com.skymiracle.server.tcpServer.TcpServer;

public class MapAntiCracker implements AntiCracker {

	private Map<String, Long> banMap = new ConcurrentHashMap<String, Long>();

	private Map<String, Item> failMap = new ConcurrentHashMap<String, Item>();

	private int banSeconds = 3600;

	private int intervalOfBan = 60;

	private int failTimesOfBan = 20;

	private String adminSecurityKey = null;

	private List<String> trustIps = new LinkedList<String>();

	private Map<String, Boolean> trustIpMap = new HashMap<String, Boolean>();

	public int getBanSeconds() {
		return banSeconds;
	}

	public void setBanSeconds(int banSeconds) {
		this.banSeconds = banSeconds;
	}

	public int getIntervalOfBan() {
		return intervalOfBan;
	}

	public void setIntervalOfBan(int intervalOfBan) {
		this.intervalOfBan = intervalOfBan;
	}

	public int getFailTimesOfBan() {
		return failTimesOfBan;
	}

	public void setFailTimesOfBan(int failTimesOfBan) {
		this.failTimesOfBan = failTimesOfBan;
	}

	public String getAdminSecurityKey() {
		return adminSecurityKey;
	}

	public void setAdminSecurityKey(String adminSecurityKey) {
		this.adminSecurityKey = adminSecurityKey;
	}

	public List<String> getTrustIps() {
		return trustIps;
	}

	public void setTrustIps(List<String> trustIps) {
		this.trustIps = trustIps;
		for (String ip : trustIps) {
			trustIpMap.put(ip, true);
		}
	}

	public MapAntiCracker() {
		// Thread: 定时检查banMap数据过期情况
		Thread banMapCheckThread = new Thread("BanMapCheckThread") {
			public void run() {
				try {
					Thread.sleep(banSeconds / 2 * 1000);
					for (Map.Entry<String, Long> entry : banMap.entrySet()) {
						String ip = entry.getKey();
						Long tm = entry.getValue();
						// 如果被ban的IP记录的时间到目前为止超过了预设的banSeconds,则从banMap中移除
						if (tm - System.currentTimeMillis() > banSeconds * 1000) {
							banMap.remove(ip);
							// 一旦ip被ban,则从failMap中移除
							failMap.remove(ip);
							Logger.info("AntiCracker: IP[" + ip
									+ "] is unbaned.");
						}
					}
				} catch (InterruptedException e) {
					Logger.error("", e);
				}
			}
		};

		banMapCheckThread.start();

		// Thread: 定时清除failMap的过期数据
		Thread failMapCheckThread = new Thread("FailMapCheckThread") {
			public void run() {
				try {
					Thread.sleep(300 * 1000); // 间隔300秒检查一次
					for (Map.Entry<String, Item> entry : failMap.entrySet()) {
						String ip = entry.getKey();
						Item item = entry.getValue();
						// 如果被item的记录时间到目前为止超过了预设的intervalOfBan的2倍,则从failMap中移除
						if (item.firstTimeStamp - System.currentTimeMillis() > intervalOfBan * 1000 * 2)
							failMap.remove(ip);
					}
				} catch (InterruptedException e) {
					Logger.error("", e);
				}
			}
		};
		failMapCheckThread.start();

	}

	public boolean doFilter(TcpConnHandler<? extends TcpServer> connHandler) {
		Long tm = banMap.get(connHandler.getRemoteIP());
		return tm == null;
	}

	public void doFilterAfterAuth(boolean isSucc, String id, String modeName,
			String remoteIp) {
		// NOTE: Ignore id and modeName, use remoteIP as key

		// DEBUG
		// if (remoteIp.equals("127.0.0.1"))
		// remoteIp = "1127.0.0.1";

		// 跳过trust IP
		Boolean isTrust = this.trustIpMap.get(remoteIp);
		if (isTrust != null && isTrust)
			return;

		// 如果结果是成功的，则直接从map中移除记录
		if (isSucc) {
			failMap.remove(remoteIp);
			return;
		}

		// 结果失败，处理方式如下...

		Item item = failMap.get(remoteIp);
		if (item == null) {
			item = new Item(remoteIp);
			failMap.put(remoteIp, item);
		}

		// 如果此次失败时间与上次reset后第一次失败记录的时间间隔大于intervalOfBan，则reset重新开始计算
		long now = System.currentTimeMillis();
		long life = now - item.firstTimeStamp;
		if (life > this.intervalOfBan * 1000) {
			item.reset();
		}

		item.failedTimes++;

		// 如果在intervalOfBan秒内，失败次数小于预设值failTimesOfBan，则返回
		if (item.failedTimes < this.failTimesOfBan) {
			return;
		}
		// 如果在intervalOfBan秒内，失败次数大于预设值failTimesOfBan，即符合了ban的条件，则处理
		banMap.put(remoteIp, System.currentTimeMillis());
		Logger.info("AntiCracker: IP[" + remoteIp
				+ "] is baned for password cracking.");

	}

	private class Item {
		// IP地址
		private String ip;

		// 失败次数
		private int failedTimes = 0;

		// 第一次失败发生的时间戳
		private long firstTimeStamp = System.currentTimeMillis();

		public Item(String ip) {
			this.ip = ip;
		}

		public void reset() {
			this.failedTimes = 0;
			this.firstTimeStamp = System.currentTimeMillis();
		}
	}

	public ArrayList<String> getBanIps() {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Long> entry : this.banMap.entrySet()) {
			list.add(entry.getKey());
		}
		return list;
	}

	public boolean isAdminSecurityKeyCheck(String inPass) {
		// 如果adminSecurityKey没设置，则不需要密码
		if (this.adminSecurityKey == null)
			return true;

		if (inPass == null)
			return false;
		inPass = inPass.trim();
		return inPass.equalsIgnoreCase(adminSecurityKey);
	}

	public boolean removeBan(String ip) {
		Long l = this.banMap.remove(ip);
		return l != null;
	}
}
