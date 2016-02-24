package com.skymiracle.util;

public class FnsBase64 {

	public static String encode(String s) {
		byte[] bs = Base64.encode(s.getBytes());
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == '/')
				bs[i] = '-';
		}
		return new String(bs);
	}

	public static String decode(String s) {
		byte[] bs = s.getBytes();
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == '-')
				bs[i] = '/';
		}
		return new String(Base64.decode(bs));
	}
}
