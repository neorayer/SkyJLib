package com.skymiracle.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewTools {

	public static String getMSize(long n) {
		float f = n / 1024 / 1024;
		return FormatOut.format(f, ".2");
	}

	public static String getTgmkSize(long n) {
		StringBuffer rsSb = new StringBuffer();
		StringBuffer sb = new StringBuffer().append(n);

		int length = sb.length();
		if (length > 12) {
			String s1 = sb.substring(0, length - 12);
			String s2 = sb.substring(length - 12 + 1, length - 12 + 2);
			rsSb.append(s1).append('.').append(s2).append('T');
		} else if (length > 9) {
			String s1 = sb.substring(0, length - 9);
			String s2 = sb.substring(length - 9 + 1, length - 9 + 2);
			rsSb.append(s1).append('.').append(s2).append('G');
		} else if (length > 6) {
			String s1 = sb.substring(0, length - 6);
			String s2 = sb.substring(length - 6 + 1, length - 6 + 2);
			rsSb.append(s1).append('.').append(s2).append('M');
		} else if (length > 3) {
			String s1 = sb.substring(0, length - 3);
			String s2 = sb.substring(length - 3 + 1, length - 3 + 2);
			rsSb.append(s1).append('.').append(s2).append('K');
		} else
			return sb.toString();

		return rsSb.toString();
	}

	public static String getStringTime(long timestamp) {
		Date date = new Date(timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static void main(String[] args) {
		System.out.println(ViewTools.getTgmkSize(3332323));
	}
}
