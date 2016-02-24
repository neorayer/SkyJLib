package com.skymiracle.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.skymiracle.logger.Logger;

public class CalendarUtil {
	public static String getLocalDateTime() {
		return getLocalDateTime("yyyy-MM-dd HH:mm:ss");
	}

	public static String getLocalDate() {
		return getLocalDateTime("yyyy-MM-dd");
	}

	public static String getLocalDayTime() {
		return getLocalDateTime("HH:mm:ss");
	}

	public static String getHms(long allSec) {
		long hour = allSec / 3600;
		long min = (allSec - hour * 3600) / 60;
		long sec = allSec - hour * 3600 - min * 60;
		return String.format("%d:%02d:%02d", hour, min, sec);
	}

	public static String getLocalDateStandard() {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		return date.toString();
	}

	/**
	 * 
	 * @param date
	 *            yyyy-MM-dd
	 * @return
	 * @throws ParseException
	 */
	public static String getDateFromDate(String dateSrc, int day, String format)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = sdf.parse(dateSrc);
		long tms = date.getTime();
		date.setTime(tms + 24 * 3600 * 1000 * day);
		return sdf.format(date);
	}

	public static String getLocalDateTime(String format) {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static long getTimeInMillis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}

	public static String getMonth() {
		return getLocalDateTime("M");
	}

	public static boolean equalsNow(long millSeconds, String pattern) {
		String date = getFormat(millSeconds, pattern);
		String today = getFormat(System.currentTimeMillis(), pattern);
		return date.equals(today);
	}

	public static boolean isToday(long millSeconds) {
		return equalsNow(millSeconds, "yyyy-MM-dd");
	}

	public static boolean isThisWeek(long millSeconds) {
		return equalsNow(millSeconds, "yyyy w");
	}

	public static String getLongFormat(long millSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millSeconds);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String getFormat(long millSeconds, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millSeconds);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String getHumanFormat(long millSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millSeconds);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static String normalParse(String s) {
		if (s == null)
			return null;
		if (s.length() == 0)
			return null;
		s = s.trim();
		s = s.replace("Mon", "");
		s = s.replace("Tue", "");
		s = s.replace("Sat", "");
		s = s.replace("Fri", "");
		s = s.replace("Wed", "");
		s = s.replace("Thu", "");
		s = s.replace("Sun", "");
		s = s.replace(",", "");
		s = s.replace("(CST)", "");
		s = s.replace("CST", "");
		s = s.replace("UTC", "");
		s = s.replace("GMT", "");
		s = s.replace("EST", "");
		s = s.replace("00800", "");
		s = s.replace("中国标准时间", "");
		int pos = s.indexOf('+');
		if (pos > 0)
			s = s.substring(0, pos);
		pos = s.indexOf('-');
		if (pos > 0)
			s = s.substring(0, pos);

		try {

			String y = "2005";
			String m = null;
			String d = null;
			String t = null;
			String[] ss = s.split(" ");
			for (int i = 0; i < ss.length; i++) {
				String si = ss[i].trim();
				if (si.length() == 0)
					continue;
				if (si.indexOf(':') > 0) {
					String[] ts = si.split(":");
					if (ts.length < 3)
						t = "00:00:00";
					else {
						StringBuffer sb = new StringBuffer();
						int h = Integer.parseInt(ts[0]);
						if (h < 10)
							sb.append("0");
						sb.append(h);
						sb.append(":");
						int mi = Integer.parseInt(ts[1]);
						if (mi < 10)
							sb.append("0");
						sb.append(mi);
						t = sb.toString();
					}
				} else if (si.indexOf("Jan") >= 0)
					m = "01";
				else if (si.indexOf("Feb") >= 0)
					m = "02";
				else if (si.indexOf("Mar") >= 0)
					m = "03";
				else if (si.indexOf("Apr") >= 0)
					m = "04";
				else if (si.indexOf("May") >= 0)
					m = "05";
				else if (si.indexOf("Jun") >= 0)
					m = "06";
				else if (si.indexOf("Jul") >= 0)
					m = "07";
				else if (si.indexOf("Aug") >= 0)
					m = "08";
				else if (si.indexOf("Sep") >= 0)
					m = "09";
				else if (si.indexOf("Oct") >= 0)
					m = "10";
				else if (si.indexOf("Nov") >= 0)
					m = "11";
				else if (si.indexOf("Dec") >= 0)
					m = "12";
				else {
					try {
					int n = Integer.parseInt(si);
					if (n < 10)
						d = "0" + n;
					else if (n < 32)
						d = "" + n;
					else if (n <= 2017)
						y = "" + n;
					}catch(NumberFormatException nfe) {
						
					}
				}
			}
			s = new StringBuffer().append(y).append('-').append(m).append('-')
					.append(d).append(' ').append(t).toString();
		} catch (Exception e) {
			Logger.error("CalendarUtil.normalParse(), s=" + s, e);
			s = null;
		}
		return s;

	}

	public static String getDateRFC822(Calendar c) {
		SimpleDateFormat fmt = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		String date = fmt.format(c.getTime());
		return date;
	}

	public static Calendar getCalendar(String year, String mon, String day,
			String hour, String min) {
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(year), Integer.parseInt(mon) - 1, Integer
				.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
		return c;
	}

	public static String dateToString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date stringToDate(String strDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算两个不同日期之间相差的天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long diffDay(Date startDate, Date endDate) {
		long timestamp = endDate.getTime() - startDate.getTime();
		return timestamp / (60 * 60 * 24 * 1000);
	}

	public static Date rollBackMonth(int num) {
		return CalendarUtil.rollBackMonth(null, num);
	}

	public static Date rollBackDay(int num) {
		return CalendarUtil.rollBackDay(null, num);
	}

	public static Date rollBackHour(int num) {
		return CalendarUtil.rollBackHour(null, num);
	}

	public static Date rollBackMonth(Date date, int num) {
		Calendar ca = Calendar.getInstance();
		if (date != null)
			ca.setTime(date);
		ca.add(Calendar.MONTH, -num);
		return ca.getTime();
	}

	public static Date rollBackDay(Date date, int num) {
		Calendar ca = Calendar.getInstance();
		if (date != null)
			ca.setTime(date);
		ca.add(Calendar.DAY_OF_MONTH, -num);
		return ca.getTime();
	}

	public static Date rollBackHour(Date date, int num) {
		Calendar ca = Calendar.getInstance();
		if (date != null)
			ca.setTime(date);
		ca.add(Calendar.HOUR, -num);
		return ca.getTime();
	}

	public static void main(String[] args) {

		// Logger.info("Begin");
		// Calendar caLast = getCalendar("2008", "7", "8", "0", "0");
		// Calendar caNow = Calendar.getInstance();
		// long sub = caLast.getTimeInMillis() - caNow.getTimeInMillis();
		// int day = (int)(sub / ( 1000 * 3600 * 24)) + 1;
		// System.out.println(day);
		//		
		// // System.out.println(normalParse("Sun, 5 Feb 2006 05:52:39 00800
		// "));
		// Logger.info("End");
		// System.out.println(CalendarUtil.getLocalDateTime("yy-MM"));
		// Date startDate=CalendarUtil.stringToDate("20090301", "yyyyMMdd");
		// Date endDate =CalendarUtil.stringToDate("20090228", "yyyyMMdd");
		// System.out.println(CalendarUtil.diffDay(startDate, endDate));

//		Calendar caLast = getCalendar("2008", "7", "8", "0", "0");
//		Date date = CalendarUtil.rollBackHour(caLast.getTime(), 1);
//		System.out.println(CalendarUtil.dateToString(date, "yyyy-MM-dd hh:mm:ss"));
		
		String s ="Wed, 5 May 2010 10:55:20 +0800";
		System.out.println(CalendarUtil.normalParse(s));
	}
}
