package com.skymiracle.util;

import com.skymiracle.logger.Logger;

public class FnsQPCodec {

	public static String encode(String s, String charset) {
		if (s == null)
			return null;
		String sEnc = s;
		try {
			sEnc = new QuotedPrintableCodec(charset).encode(s);
		} catch (Exception e) {
			Logger.error("FnsQPCodec.encode(charset)", e);
		}
		sEnc = sEnc.replace(" ", "vd6q1q7l");
		return sEnc;
	}

	public static String decode(String s, String charset) {
		if (s == null)
			return null;
		s = s.replace("vd6q1q7l", " ");
		String sDec = s;
		try {
			sDec = new QuotedPrintableCodec(charset).decode(s);
		} catch (Exception e) {
			Logger.error("FnsQPCodec.decode(charset)", e);
		}
		return sDec;
	}

	public static String encode(String s) {
		return encode(s, "GB2312");
	}

	public static String decode(String s) {
		return decode(s, "GB2312");
	}

}
