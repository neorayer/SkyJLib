package com.skymiracle.util;

import java.io.UnsupportedEncodingException;

public class AsciiCodec {

	public static String encode(String s) {
		StringBuffer sb = new StringBuffer();
		byte[] bytes = s.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			sb.append("%");
			if (bytes[i] > 0)
				sb.append(bytes[i]);
			else
				sb.append(bytes[i] + 256);
		}
		return sb.toString();
	}

	public static String decode(String s) throws UnsupportedEncodingException {
		s = s.replaceFirst("%", "");
		String[] ss = s.split("%");
		byte[] bytes = new byte[ss.length];
		for (int i = 0; i < ss.length; i++) {
			int intValue = Integer.parseInt(ss[i]);
			bytes[i] = intValue > 127 ? (byte) (intValue - 256)
					: (byte) intValue;
		}
		return new String(bytes);
	}

}
