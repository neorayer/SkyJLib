package com.skymiracle.server.tcpServer.mailServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.skymiracle.io.TextFile;
import com.skymiracle.mime.ReportMimeCreater;
import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;
import com.skymiracle.util.Base64;
import com.skymiracle.util.Rfc2047Codec;

public class AutoMailMaker {

	public final static int MAX_LOOP = 100;

	public final static String B64_AUTOREPLY = "=?gb2312?b?19S2r7vYuLSjug==?=";

	public final static String AUTOREPLY = "自动回复：";

	public static LinkedList<String> getForwardHeaderList(
			List<String> srcHeaderlineList, String forwardAddr, String srcFrom,
			String srcTo) {
		LinkedList<String> destHeaderlineList = new LinkedList<String>();
		String o_from = null;
		String o_to = null;
		boolean existLoop = false;
		boolean existOFrom = false;
		boolean existOTo = false;

		ListIterator it = srcHeaderlineList.listIterator();
		String line = null;
		while (it.hasNext()) {
			line = (String) it.next();
			if (line.startsWith("X-WPX-Loop:")) {
				existLoop = true;
				int loopnum = 0;
				try {
					loopnum = Integer.parseInt(line.substring(11).trim());
				} catch (Exception e) {
				}
				if (loopnum > MAX_LOOP) {
					return null;
				}
				loopnum++;
				it.set("X-WPX-Loop: " + loopnum);
			}
			if (line.startsWith("X-WPX-OFROM:")) {
				existOFrom = true;
				o_from = line.substring(12).trim();
			}
			if (line.startsWith("X-WPX-OTO:")) {
				existOTo = true;
				o_to = line.substring(9).trim();
			}
			if (existOTo && existOFrom) {
				if (o_from.equalsIgnoreCase(srcTo)) {
					// System.out.println( "LOOP! because mail from the origin
					// to the origin");
					return null;
				}
			}
			if (line.startsWith("To:")) {
//				it.set("To: " + forwardAddr);
				it.set("To: " + line.substring(2).trim());
			}
		}
		destHeaderlineList.addAll(srcHeaderlineList);
		if (!existLoop)
			destHeaderlineList.addFirst("X-WPX-Loop: 1");
		if (!existOTo)
			destHeaderlineList.addFirst("X-WPX-OTO: " + srcTo);
		if (!existOFrom)
			destHeaderlineList.addFirst("X-WPX-OFROM: " + srcFrom);

		return destHeaderlineList;
	}

	public static List<String> getReplyHeader(
			Map<String, String> headerElementMap, String srcFrom, String srcTo) {
		LinkedList<String> destHeaderlineList = new LinkedList<String>();
		
		int loopnum = 1;
		String o_loop = headerElementMap.get("X-WPX-Loop".toLowerCase());
		if(o_loop != null) {
			try {
				loopnum = Integer.parseInt(o_loop);
			} catch (Exception e) {
			}
			if (loopnum > MAX_LOOP) {
				return null;
			}
			loopnum++;
		}
		
		String o_from = headerElementMap.get("X-WPX-OFROM".toLowerCase());
		String o_to = headerElementMap.get("X-WPX-OTO".toLowerCase());
		if (o_from != null && o_to != null && o_from.equalsIgnoreCase(srcTo)) {
			// System.out.println( "LOOP! because mail from the origin
			// to the origin");
			return null;
		}
		
		// create a header
		destHeaderlineList.add("From: " + srcTo);
		destHeaderlineList.add("To: " + srcFrom);
		try {
			String subject = headerElementMap.get("Subject".toLowerCase());
			String x = AUTOREPLY + Rfc2047Codec.decode(subject);
			destHeaderlineList.add("Subject: =?UTF-8?B?"
					+ new String(Base64.encode(x.getBytes("UTF-8"))) + "?=");
		} catch (UnsupportedEncodingException e) {
		}
		destHeaderlineList.add("Content-Type: text/html;");
		destHeaderlineList.add("\tcharset=\"UTF-8\"");
		destHeaderlineList.add("Content-Transfer-Encoding: Base64");
		destHeaderlineList.add("Date: " + new Date());
		destHeaderlineList.addFirst("X-WPX-Loop: " + loopnum);
		if (o_from== null)
			destHeaderlineList.addFirst("X-WPX-OFROM: " + srcFrom);
		if (o_to == null)
			destHeaderlineList.addFirst("X-WPX-OTO: " + srcTo);
		
		return destHeaderlineList;
	}

	public static final String ALERT_S = "=?GB2312?B?v9W85M7Ayr++r7jm0MU=?=";

	public static final String ALERT_C = "您的空间已使用xM,已经达到您设定的警告线yM；";

	public static boolean createAlertMail(String filePath, long usedSize,
			long spaceAlert) {
		usedSize = usedSize / 1024 / 1024;
		spaceAlert = spaceAlert / 1024 / 1024;
		ArrayList<String> destlineList = new ArrayList<String>();
		destlineList.add("From: \"postermaster\"<>");
		destlineList.add("To: <>");
		destlineList.add("Subject: =?GB2312?B?v9W85M7Ayr++r7jm0MU=?=");
		destlineList.add("Content-Type: text/plain;");
		destlineList.add("\tcharset=\"UTF-8\"");
		destlineList.add("Content-Transfer-Encoding: Base64");
		destlineList.add("Date: " + new Date());
		destlineList.add("\r\n\r\n");
		try {
			destlineList.add(new String(Base64.encode(ALERT_C.replaceAll("x",
					usedSize + "").replaceAll("y", spaceAlert + "").getBytes(
					"UTF-8"))));
			TextFile.save(filePath, destlineList);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static List<String> getMailHeaderList(List<String> srcDatalineList) {
		List<String> list = new LinkedList<String>();
		for(String line: srcDatalineList) {
			if (line.length() == 0)
				break;
			list.add(line);
			
		}
		return list;
	}
	
	public static int getMailBodyBeginInList(List<String> srcDatalineList) {
		String line = null;
		for (int i = 0; i < srcDatalineList.size(); i++) {
			line = srcDatalineList.get(i);
			if (line.length() == 0)
				return i + 1;
		}
		return srcDatalineList.size() + 1;
	}
	
	public static HashMap<String, String> getMailHeaderElementMap(List<String> mailHeader) {
		ArrayList<String> cellList = new ArrayList<String>();
		for (int i = 0; i < mailHeader.size();) {
			StringBuffer tmp = new StringBuffer();
			tmp.append(mailHeader.get(i));
			i++;
			if (i < mailHeader.size()) {
				String line = mailHeader.get(i);
				while (line.startsWith("\t") || line.startsWith(" ")) {
					tmp.append(line.trim());
					i++;
					if (i >= mailHeader.size())
						break;
					line = mailHeader.get(i);
				}
			}
			cellList.add(tmp.toString());
		}
		
		HashMap<String, String> hm = new HashMap<String, String>();
		for (int i = 0; i < cellList.size(); i++) {
			String tmp = cellList.get(i);
			if (tmp.indexOf(":") == -1)
				continue;
			int pos = tmp.indexOf(":");
			String key = tmp.substring(0, pos).toLowerCase().trim();
			String value = tmp.substring(pos + 1).trim();
			if (hm.get(key) != null)
				value += "|" + hm.get(key);
			hm.put(key, value);
		}
		return hm;
	}

	public static final int RECEIVED_LOOP = 100;

	public static boolean receivedMailLoopCheck(List<String> dataLineList) {
		String received = getMailHeaderElementMap(getMailHeaderList(dataLineList))
		.get("received");
		if (received != null)
			if (received.split("\\|").length > RECEIVED_LOOP)
				return false;
			else
				return true;
		return true;
	}

	public static List<String> getBounceMail(String from, String to, String o_to,
			String content, String reason, List<String> dataLineList)
			throws Exception {
		ReportMimeCreater rmc = new ReportMimeCreater(from, to, o_to,
				"系统退信");
		rmc.setContent(content);
		rmc.setReason(reason);
		StringBuffer sb = new StringBuffer();
		List<String> ll = getMailHeaderList(dataLineList);
		for(String l:ll)
			sb.append(l).append("\r\n");
		rmc.setMessage(ReportMimeCreater.MESSAGE_TYPE_HEADER, sb);

		return rmc.getReport("UTF-8");
	}

	public static List<String> getBounceMail(String from, String to, String o_to,
			String content, String reason, MailMessage mailMessage)
			throws Exception {

		return getBounceMail(from, to, o_to, content, reason, mailMessage
				.getDataLineList());
	}

}
