package com.skymiracle.util;

import java.io.*;
public class Tester {

	public static void main(String[] args) throws Exception {
		String sUTF8 = "我爱 北 京 天安门% ! ?";
		System.out.println(sUTF8);
		String sGBK = new String(sUTF8.getBytes("UTF-8"), "GBK");
		System.out.println(sGBK);

		FileInputStream fis = new FileInputStream("/tmp/test.txt");
		InputStreamReader isr = new InputStreamReader(fis);
		isr.close();
		fis.read();
	}
}
