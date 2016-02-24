/**
 * 
 */
package com.skymiracle.test;

/**
 * @author pengm
 * 
 */

import java.io.UnsupportedEncodingException;

public class Test {

	public static void main(String[] a) throws UnsupportedEncodingException {
		byte[] bs = new byte[]{(byte)0x2D ,(byte)0xEF ,(byte)0xBF ,(byte)0xBD ,(byte)0xEF ,(byte)0xBF ,(byte)0xBD ,(byte)0xEF ,(byte)0xBF ,(byte)0xBD ,(byte)0xEF ,(byte)0xBF ,(byte)0xBD ,(byte)0x20 };
		String s = new String(bs, "GB2312");
		System.out.println(s);
	}
}
