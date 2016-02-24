package com.skymiracle.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.skymiracle.util.CalendarUtil;

public class AnalyzedLogger {

	// private static long beginTime = System.currentTimeMillis();

	private static String logFilePath = "./log.txt";

	public final static int TARGET_STDOUT = 0;

	public final static int TARGET_SIMPLEFILE = 1;

	private static int target = TARGET_STDOUT;

	private static boolean isLogDateSep = true;

	public static void setLogFilePath(String filePath) {
		logFilePath = filePath;
	}

	public static void setDateSep(boolean isDateSep) {
		isLogDateSep = isDateSep;
	}

	public static void setTarget(int logTarget) {
		target = logTarget;
	}

	public static void analyz(String msg) {
		msg("Info", msg);
	}

	public static void analyz(String type, String msg) {
		msg(type, msg);
	}

	private static void msg(String type, String msg) {
		msg(type, new StringBuffer(msg));
	}

	private static void msg(String type, StringBuffer msgBuf) {
		msg(type, msgBuf, null);
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat(".yyyy-MM-dd");

	private static String getDayLogPath(String logFilePath) {
		String today = sdf.format(new Date());
		String newLogFilepath = logFilePath;
		File file = new File(logFilePath);
		String path = file.getParent() + "/";
		String name = file.getName();
		int pos = name.lastIndexOf(".");

		if (pos > 0) {
			String ext = name.substring(pos);
			newLogFilepath = path + name.substring(0, pos) + today + ext;
		} else {
			newLogFilepath = path + name + today;
		}
		return newLogFilepath;
	}

	private static void msg(String type, StringBuffer msgBuf, Exception e,
			boolean isDateSpe) {
		StringBuffer sb = new StringBuffer();
		long nowTime = System.currentTimeMillis();
		sb.append(CalendarUtil.getLocalDateTime()).append(" ");
		// sb.append(nowTime - beginTime).append("[");
		// .append(Thread.currentThread().getId()).append("]").append(" - ");
		sb.append(type).append(": ").append(msgBuf);

		PrintStream ps = null;
		if (target == AnalyzedLogger.TARGET_SIMPLEFILE)
			try {
				String realLogFilePath = isDateSpe ? getDayLogPath(logFilePath)
						: logFilePath;
				FileOutputStream fos = new FileOutputStream(realLogFilePath,
						true);
				ps = new PrintStream(fos);
			} catch (FileNotFoundException e1) {
				ps = System.out;
			}
		else
			ps = System.out;

		ps.print(sb.append("\r\n").toString());
		if (e != null) {
			ps.print(e + "\r\n");
			e.printStackTrace(ps);
		}
		if (ps != System.out)
			ps.close();
	}

	/**
	 * if the target equals TARGET_STDOUT ,print the message into the System.out
	 * if the target equals TARGET_SIMPLEFILE ,print the message into the log
	 * type is not used
	 * 
	 * @param type
	 * @param msgBuf
	 * @param e
	 */
	private static void msg(String type, StringBuffer msgBuf, Exception e) {
		msg(type, msgBuf, e, isLogDateSep);
	}

	public static void main(String[] args) {
		AnalyzedLogger.setTarget(AnalyzedLogger.TARGET_STDOUT);
		AnalyzedLogger.setLogFilePath("/tmp/test");
		AnalyzedLogger.analyz("1");
		for (int i = 0; i < 100; i++) {
			AnalyzedLogger.analyz("SMTP", "" + i);
		}

	}

}
