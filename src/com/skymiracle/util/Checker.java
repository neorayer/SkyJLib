package com.skymiracle.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {

	private static Pattern mfAddressPattern = Pattern
			.compile("(<>|<(@[\\w-]+(\\.[\\w-]+)+:|@[\\w-]+(\\.[\\w-]+)+(,@[\\w-]+(\\.[\\w-]+)+)*:)?([\\w-\\.+=]+@[\\w-]+(\\.[\\w-]+)+)>)((\\s\\w+(=[\\x21-\\x3c\\x3e-\\xFF]+)?)*)");

	private static Pattern mfAddressPatternNoStrick = Pattern
	.compile("(<>|(@[\\w-]+(\\.[\\w-]+)+:|@[\\w-]+(\\.[\\w-]+)+(,@[\\w-]+(\\.[\\w-]+)+)*:)?([\\w-\\.+=]+@[\\w-]+(\\.[\\w-]+)+))((\\s\\w+(=[\\x21-\\x3c\\x3e-\\xFF]+)?)*)");

	public static boolean isGoodEmailAddress(String s) {
		Matcher mf = mfAddressPattern.matcher(s);
		return mf.matches();
	}

	public static boolean isGoodEmailAddress(String s, boolean isStrick) {
		if (isStrick) {
			Matcher mf = mfAddressPattern.matcher(s);
			return mf.matches();
		}
		Matcher mf = mfAddressPatternNoStrick.matcher(s);
		return mf.matches();
	}

	public static boolean isGoodIPv4(String ip) {
		if (ip == null)
			return false;
		String[] ss = ip.split("\\.");
		if (ss.length != 4)
			return false;
		for (int i = 0; i < 4; i++) {
			int v = Integer.parseInt(ss[i]);
			if (v > 255)
				return false;
			if (v < 0)
				return false;
		}
		return true;
	}
//
//	public static boolean isCorrectMailAddress(String s, boolean isMf) {
//		if (isMf) {
//			Matcher mf = mfAddressPattern.matcher(s);
//			if (mf.matches())
//				return true;
//		} else {
//			Matcher rt = rtAddressPattern.matcher(s);
//			if (rt.matches())
//				return true;
//		}
//		return false;
//	}
//
//	public static RFC2821Session getCorrectRFC2821(String s, boolean isMf) {
//		RFC2821Session rfc2821 = null;
//		if (isMf) {
//			Matcher mf = mfAddressPattern.matcher(s);
//			if (mf.matches()) {
//				rfc2821 = new RFC2821Session();
//				rfc2821.setSessionStr(s);
//				rfc2821.setMF(isMf);
//				if (mf.group(2) != null) {
//					String rs = mf.group(2).substring(0,
//							mf.group(2).length() - 1);
//					rfc2821.setSourceRoutes(rs.split(","));
//				}
//				rfc2821.setAddress(mf.group(7));
//				if (mf.group(7) == null)
//					rfc2821.setAddress("Postmaster");
//				String param = mf.group(9);
//				if (param != null) {
//					HashMap hm = new HashMap();
//					String[] ps = param.split(" ");
//					for (int i = 0; i < ps.length; i++) {
//						int pos = ps[i].indexOf("=");
//						if (pos != -1)
//							hm.put(ps[i].substring(0, pos), ps[i]
//									.substring(pos + 1));
//						else
//							hm.put(ps[i], null);
//					}
//					rfc2821.setEsmtp_param(hm);
//				}
//			}
//		} else {
//			Matcher rt = rtAddressPattern.matcher(s);
//			if (rt.matches()) {
//				rfc2821 = new RFC2821Session();
//				rfc2821.setSessionStr(s);
//				rfc2821.setMF(isMf);
//				if (rt.group(2) != null) {
//					String rs = rt.group(2).substring(0,
//							rt.group(2).length() - 1);
//					rfc2821.setSourceRoutes(rs.split(","));
//				}
//				rfc2821.setAddress(rt.group(7));
//				if (rt.group(7) == null)
//					rfc2821.setAddress("Postmaster");
//				String param = rt.group(9);
//				if (param != null) {
//					HashMap hm = new HashMap();
//					String[] ps = param.split(" ");
//					for (int i = 0; i < ps.length; i++) {
//						int pos = ps[i].indexOf("=");
//						if (pos != -1)
//							hm.put(ps[i].substring(0, pos), ps[i]
//									.substring(pos + 1));
//						else
//							hm.put(ps[i], null);
//					}
//					rfc2821.setEsmtp_param(hm);
//				}
//			}
//		}
//		return rfc2821;
//	}

//	public static String getCorrectMailAddress(String s, boolean isMf) {
//		String addr = null;
//		if (isMf) {
//			Matcher mf = mfAddressPattern.matcher(s);
//			if (mf.matches()) {
//				addr = mf.group(7);
//				if (addr == null)
//					addr = "";
//			}
//		} else {
//			Matcher rt = rtAddressPattern.matcher(s);
//			if (rt.matches()) {
//				addr = rt.group(7);
//				if (addr == null)
//					addr = "Postmaster";
//			}
//		}
//		return addr;
//	}
//
//	public static void main(String[] args) {
//		String rfc2821 = null;
//		rfc2821 = getCorrectMailAddress(
//				"<@163.com,@126.com:-a_a.@a.com> size=911 ba=8*", false);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress("<aa_a.@a.com> size=911 ba=8*", false);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress("<Postmaster> size=911 ba=8*", false);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress("<>", true);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress("<a.@a.com> size=911 ba=8*", true);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress("<@163.com:a.@a.com> size=911 ba=8*",
//				true);
//		System.out.println(rfc2821);
//		rfc2821 = getCorrectMailAddress(
//				"<neorayer+caf_=zhourui=skymiracle.com@gmail.com>", false);
//		System.out.println(rfc2821);
//	}
}
