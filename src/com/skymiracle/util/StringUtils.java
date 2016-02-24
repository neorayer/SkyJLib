package com.skymiracle.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class StringUtils {

	public static String toFirstCap(String str) {
		String firstLetter = str.substring(0, 1);
		str = str.replaceFirst(firstLetter, firstLetter.toUpperCase());
		return str;
	}

	public static String toFirstLow(String str) {
		String firstLetter = str.substring(0, 1);
		str = str.replaceFirst(firstLetter, firstLetter.toLowerCase());
		return str;
	}

	public static boolean isIncluded(String[] srcStrs, String dest) {
		if (srcStrs == null)
			return false;
		for (String src : srcStrs)
			if (src.equals(dest))
				return true;
		return false;
	}

	public static String getFileExt(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return defExt;
	}
	
	public static Object toObject(String s, Class<?> type) {
		Object value = null;
		if (type == String.class) {
			value = s;
		} else if (type == StringBuffer.class) {
			value = new StringBuffer(s);
		} else if (type == long.class)
			value = new Long(Long.parseLong(s));
		else if (type == int.class)
			value = new Integer(Integer.parseInt(s));
		else if (type == short.class)
			value = new Short(Short.parseShort(s));
		else if (type == byte.class)
			value = new Byte(Byte.parseByte(s));
		else if (type == double.class)
			value = new Double(Double.parseDouble(s));
		else if (type == float.class)
			value = new Float(Float.parseFloat(s));
		else if (type == boolean.class)
			value = new Boolean(Boolean.parseBoolean(s));
		else if (type.isEnum()) {
			value = Enum.valueOf((Class<Enum>) type, s);
		}
		return value;
	}

	/**
	 * 顺序从values里取出一个不为空白的。
	 * 空白的意思是 null或者 trim().length == 0
	 * 
	 * @param values
	 * @return
	 */
	public static String or(String... values) {
		for(String v: values) {
			if (v == null)
				continue;
			v = v.trim();
			if (v.length() == 0)
				continue;
			if (" ".equals(v))
				continue;
			if ("".equals(v))
				continue;
			return v;
		}
		return null;
	}

	public static String join(Collection<?> objects, String sep) {
		int len = objects.size();
		if (len == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		int i=0;
		for(Object o: objects) {
			sb.append(o.toString());
			if (++i < len)
				sb.append(sep);
		}
		return sb.toString();
	}
	

	public static String join(String[] ss, String sep) {
		int len = ss.length;
		if (len == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		int i=0;
		for(String s: ss) {
			sb.append(s);
			if (++i < len)
				sb.append(sep);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "编码";
		System.out.println(Character.SIZE);
		System.out.println(Byte.SIZE);
		System.out.println(s.toCharArray().length);
		byte[] bs = {(byte)-43,(byte)-48};
		System.out.println(new String(bs, "GBK"));
	}

}
