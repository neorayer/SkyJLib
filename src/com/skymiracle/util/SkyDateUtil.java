package com.skymiracle.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SkyDateUtil {

	public static String getDateWithTime() {
		Calendar can = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(can.getTime());
	}

	public static String getDateTimeP() {
		Calendar can = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(can.getTime());
	}

	public static String getTime() {
		Calendar can = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(can.getTime());
	}

	public static String getDate() {
		Calendar can = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(can.getTime());
	}

	public static String addDate(int i) {
		Calendar can = Calendar.getInstance();
		can.add(Calendar.DAY_OF_MONTH, i);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(can.getTime());
	}

	public static String addMonth(int n) {
		Calendar can = Calendar.getInstance();
		can.add(Calendar.MONTH, n);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(can.getTime());
	}

	public static long getTimestamp() {
		return Calendar.getInstance().getTimeInMillis() / 1000;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(addMonth(2));
	}

}
