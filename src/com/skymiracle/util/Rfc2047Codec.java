package com.skymiracle.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rfc2047Codec {

	public class CodeRes {
		public String charset = null;
		public String content = null;
		
		public CodeRes(String charset, String content) {
			super();
			this.charset = charset;
			this.content = content;
		}
	}
	
	private static Pattern p = Pattern
			.compile("(=\\?([^?]+)\\?[BbQq]\\?([^?]*)\\?=)+?");

	private static String REPLACER_$ = "1w2q3edc";

	private static String REPLACER_BIAS = "3waxfds4";

	public CodeRes decodeRes(String s) {
		CodeRes res = new CodeRes(null, s);
		if (s == null) {
			res.charset = "ISO-8859-1";
			res.content = "";
			return res;
		}
		Matcher m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		try {
			while (m.find()) {
				CodeRes subRes = decodeSingleRes(m.group());
				if (res.charset == null)
					res.charset = subRes.charset;
				String tmp = subRes.content;
				tmp = tmp.replaceAll("\\$", REPLACER_$);
				tmp = tmp.replaceAll("\\\\", REPLACER_BIAS);
				m.appendReplacement(sb, tmp);
			}
			m.appendTail(sb);
		} catch (Exception e) {
			res.charset = "ISO-8859-1";
			res.content = s;
			return res;
		}
		res.content = (sb.toString()).trim().replaceAll(REPLACER_$, "\\$").replaceAll(
				REPLACER_BIAS, "\\\\");
		return res;
	}

	private CodeRes decodeSingleRes(String s) {
		CodeRes res = new CodeRes("ISO-8859-1", s);
		String[] ss = s.split("\\?");
		if (ss.length < 5) {
			res.content = s;
			return res;
		}

		String charset = ss[1];
		res.charset = charset;
		String codeType = ss[2];
		String content = ss[3];
		if (codeType.toLowerCase().equals("b")) {
			try {
				s = new String(Base64.decode(content), charset);
			} catch (Exception e) {
				res.content = s;
				return res;
			}
		} else if (codeType.toLowerCase().equals("q")) {
			try {
				s = (new QuotedPrintableCodec(charset)).decode(content);
			} catch (Exception e) {
				res.content = s;
				return res;
			}
		}
		res.content = s;
		return res;
	}
	
	public static String decode(String s) {
		if (s == null)
			return "";
		Matcher m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		// String[] parts = split(s);
		// for (int i=0; i<parts.length; i++) {
		// m = p.matcher(parts[i]);
		// if( m.find()){
		// sb.append(parts[i].substring(0,m.start()));
		// String part = Rfc2047Codec.decodeSingle(m.group().trim());
		// sb.append(part);
		// sb.append(parts[i].substring(m.end()));
		// }else {
		// sb.append(parts[i]);
		// }
		// }
		try {
			while (m.find()) {
				String tmp = Rfc2047Codec.decodeSingle(m.group());
				tmp = tmp.replaceAll("\\$", REPLACER_$);
				tmp = tmp.replaceAll("\\\\", REPLACER_BIAS);
				m.appendReplacement(sb, tmp);
			}
			m.appendTail(sb);
		} catch (Exception e) {
			return s;
		}
		return (sb.toString()).trim().replaceAll(REPLACER_$, "\\$").replaceAll(
				REPLACER_BIAS, "\\\\");
	}

	private static String decodeSingle(String s) {
		String[] ss = s.split("\\?");
		if (ss.length < 5)
			return s;

		String charset = ss[1];
		if (charset.equalsIgnoreCase("gb2312"))
			charset = "GBK";
		String codeType = ss[2];
		String content = ss[3];
		if (codeType.toLowerCase().equals("b")) {
			try {
				s = new String(Base64.decode(content), charset);
			} catch (Exception e) {
				return s;
			}
		} else if (codeType.toLowerCase().equals("q")) {
			try {
				s = (new QuotedPrintableCodec(charset)).decode(content);
			} catch (Exception e) {
				return s;
			}
		}
		return s;

	}

	public static void main(String[] args) {
		// System.out.println(decode("aaa=?gb2312?B?y/W2zMv1tszL9bbMIMTqt/m2yLXEtdrI/cW2v6i/ycv1tszL9bbMy/U=?=xxxxxxx=?gb2312?B?tszL9bbMy/W2zMj2ILrZutm31r3Ht723qLbUt73L7bXAvtbI9ra+y9g=?=
		// =?gb2312?B?tPOjrMurtPLI/LTytt/gwrTyy+MuZG9j?="));
		// System.out.println(decode("aaaa bbb cccc"));
		// System.out.println(decode("aaaa bb?=b cccc?="));
		// System.out.println(decode(" "));
//		System.out
//				.println(decode(" =?GB2312?B?vbujusjLysKyv7i61PDIy8rVXA==?= AD \\"));
//		System.out
//				.println(decode("=?GB2312?B?tdrSu7j216jStbzGy+O7+s28yunBrNTYxrW1wL3xyNXNxrP2IL7bus+z9rDmvee1xLvwyMjX99Xfus2z9rDmyefU2s/fvbvB9w==?="));
//		System.out
//				.println(decode(" =?gb2312?Q?IBM_developerWorks_=D6=D0=B9=FA=CD=F8=D5=BE=CA=B1=CA=C2?==?gb2312?Q?=CD=A8=D1=B6=A3=BA=B5=DA_261_=C6=DA_-_[2006-06-06]?="));
//		
//		System.out.println(decode(" =?utf-8?B?MjAxMOW5tOWFrOWPuOebrj/okL3lnLDkuI7mv4A75Yqx5oA/5qC45pa55qEA?=       =?utf-8?B?6K4/NDczODIzMjMy?="));
		
		
		System.out.println(decode("=?gb2312?B?Pz8/Pw==?="));
	}
	

}
