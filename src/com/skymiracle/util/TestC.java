package com.skymiracle.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.skymiracle.io.TextFile;

public class TestC {
	public static void main(String[] args) throws IOException {
		// ArrayList<String> list = TextFile
		// .loadLinesList(
		// "C:\\Documents and Settings\\tianliang\\My
		// Documents\\下载\\7ddd4668-3e5c-4253-a667-57804abd91cd!2,S", "utf-8");
		// for (String s : list)
		// System.out.println(new String(s.getBytes("utf-8"), "gb2312"));
		// String s = "閿熸枻鎷烽敓鏂ゆ嫹1 ";
		// System.out.println(new String(s.getBytes("utf-8"), "gb2312"));

		String s = "锟斤拷锟斤拷2 ";
		String s2 = new String(s.getBytes(), "gbk");
		System.out.println(s2);
		String s3 = new String(s.getBytes(), "utf-8");
		System.out.println(s3);
		String s4 = new String(s.getBytes(), "ISO-8859-1");
		System.out.println(s4);
		String s5 = new String(s.getBytes(), "gb2312");
		System.out.println(s5);
	}
}
