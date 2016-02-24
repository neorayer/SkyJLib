package com.skymiracle.util;

public class StringArray {

	public static String join(String[] ss) {
		StringBuffer sb = new StringBuffer();
		for (String s : ss)
			sb.append(s);
		return sb.toString();
	}
}
