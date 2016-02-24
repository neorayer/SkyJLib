package com.skymiracle.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.skymiracle.util.CalendarUtil;

public class Logger {
	public final static int LEVEL_NONE = 0;

	public final static int LEVEL_FATALERROR = 1;

	public final static int LEVEL_ERROR = 2;

	public final static int LEVEL_WARN = 3;

	public final static int LEVEL_INFO = 4;

	public final static int LEVEL_DEBUG = 5;

	public final static int LEVEL_DETAIL = 6;

	private static int level = LEVEL_INFO;

	public static String levelString = "INFO";

	private static long beginTime = System.currentTimeMillis();

	private static String logFilePath = "./log.txt";

	public final static int TARGET_STDOUT = 0;

	public final static int TARGET_SIMPLEFILE = 1;

	private static int target = TARGET_STDOUT;

	private static boolean isLogDateSep = true;
	
	private static boolean isHideSQL = false;
	
	public static void setLoggerConf(LoggerConf loggerConf) {
		Logger.setLevel(loggerConf.getLevel());
		Logger.setLogFilePath(loggerConf.getSimpleFilePath());
		Logger.setTarget(loggerConf.getTarget());
		Logger.isHideSQL = loggerConf.getIsHideSQL();
	}

	public static boolean getIsHideSQL() {
		return Logger.isHideSQL ; 
	}
	
	public static void setLogFilePath(String filePath) {
		System.out.println("Set logger file path=" + filePath);
		logFilePath = filePath;
	}

	public static void setDateSep(boolean isDateSep) {
		isLogDateSep = isDateSep;
	}

	private static String getLogTargetName() {
		switch (target) {
		case TARGET_STDOUT:
			return "STDOUT";
		case TARGET_SIMPLEFILE:
			return "SIMPLEFILE";
		}
		return "TARGET_STDOUT";
	}

	public static void setTarget(String targetName) {
		if (targetName.equalsIgnoreCase("STDOUT"))
			setTarget(TARGET_STDOUT);
		else if (targetName.equalsIgnoreCase("SIMPLEFILE"))
			setTarget(TARGET_SIMPLEFILE);
		else
			setTarget(TARGET_STDOUT);
	}

	public static void setTarget(int logTarget) {
		target = logTarget;
		System.out.println("Set logger target = " + getLogTargetName());
	}

	public static void setLevel(String loggerLevel) {
		if (null == loggerLevel)
			return;
		if (loggerLevel.equalsIgnoreCase("DETAIL"))
			Logger.setLevel(Logger.LEVEL_DETAIL);
		else if (loggerLevel.equalsIgnoreCase("DEBUG"))
			Logger.setLevel(Logger.LEVEL_DEBUG);
		else if (loggerLevel.equalsIgnoreCase("INFO"))
			Logger.setLevel(Logger.LEVEL_INFO);
		else if (loggerLevel.equalsIgnoreCase("WARN"))
			Logger.setLevel(Logger.LEVEL_WARN);
		else if (loggerLevel.equalsIgnoreCase("ERROR"))
			Logger.setLevel(Logger.LEVEL_ERROR);
		else if (loggerLevel.equalsIgnoreCase("FATALERROR"))
			Logger.setLevel(Logger.LEVEL_FATALERROR);
		else if (loggerLevel.equalsIgnoreCase("NONE"))
			Logger.setLevel(Logger.LEVEL_NONE);
	}

	public static void setLevel(int loggerLevel) {
		level = loggerLevel;
		switch (level) {
		case Logger.LEVEL_DETAIL:
			levelString = "DETAIL";
			break;
		case Logger.LEVEL_DEBUG:
			levelString = "DEBUG";
			break;
		case Logger.LEVEL_INFO:
			levelString = "INFO";
			break;
		case Logger.LEVEL_WARN:
			levelString = "WARN";
			break;
		case Logger.LEVEL_ERROR:
			levelString = "ERROR";
			break;
		case Logger.LEVEL_FATALERROR:
			levelString = "FATAL";
			break;
		case Logger.LEVEL_NONE:
			levelString = "NONE";
			break;
		default:
			break;
		}
		System.out.println("Set logger Level: " + levelString);
	}

	public static void info(String msg) {
		if (level >= Logger.LEVEL_INFO)
			msg("Info  ", msg);
	}

	public static void info(StringBuffer msgBuf) {
		if (level >= Logger.LEVEL_INFO)
			msg("Info  ", msgBuf);
	}

	public static void info(StringBuffer msgBuf, Throwable e) {
		if (level >= Logger.LEVEL_INFO)
			msg("Info  ", msgBuf, e);
	}

	public static void debug(String msg, Throwable e) {
		if (level >= Logger.LEVEL_DEBUG)
			msg("Debug ", msg, e);
	}

	public static void debug(String msg) {
		if (level >= Logger.LEVEL_DEBUG)
			msg("Debug ", msg);
	}

	public static void debug(StringBuffer msgBuf) {
		if (level >= Logger.LEVEL_DEBUG)
			msg("Debug ", msgBuf);
	}

	public static void detail(String msg) {
		if (level >= Logger.LEVEL_DETAIL)
			msg("Detail", msg);
	}

	public static void detail(String msg, Throwable e) {
		if (level >= Logger.LEVEL_DETAIL)
			msg("Detail", msg, e);
	}

	public static void detail(StringBuffer msgBuf) {
		if (level >= Logger.LEVEL_DETAIL)
			msg("Detail", msgBuf);
	}

	public static void warn(StringBuffer msg) {
		if (level >= Logger.LEVEL_WARN)
			msg("Warn  ", msg);
	}

	public static void warn(String msg, Throwable e) {
		if (level >= Logger.LEVEL_WARN)
			msg("Warn  ", msg, e);
	}

	public static void warn(String msg) {
		if (level >= Logger.LEVEL_WARN)
			msg("Warn  ", msg);
	}

	public static void warn(StringBuffer msgBuf, Throwable e) {
		if (level >= Logger.LEVEL_WARN)
			msg("Warn  ", msgBuf, e);
	}

	public static void error(String msg) {
		if (level >= Logger.LEVEL_ERROR)
			msg("Error ", msg);
	}

	public static void error(StringBuffer msgBuf) {
		if (level >= Logger.LEVEL_ERROR)
			msg("Error ", msgBuf);
	}

	public static void error(String msg, Throwable e) {
		if (level >= Logger.LEVEL_ERROR)
			msg("Error ", msg, e);
	}

	public static void error(StringBuffer msgBuf, Throwable e) {
		if (level >= Logger.LEVEL_ERROR)
			msg("Error ", msgBuf, e);
	} 

	public static void fatalError(String msg, Throwable e) {
		if (level >= Logger.LEVEL_FATALERROR)
			msg("Fatal ", msg, e);
	}

	public static void fatalError(StringBuffer msgBuf, Throwable e) {
		if (level >= Logger.LEVEL_FATALERROR)
			msg("Fatal ", msgBuf, e);
	}

	public static void fatalError(String msg) {
		if (level >= Logger.LEVEL_FATALERROR)
			msg("Fatal ", msg);
	}

	public static void fatalError(StringBuffer msgBuf) {
		if (level >= Logger.LEVEL_FATALERROR)
			msg("Fatal ", msgBuf);
	}

	private static void msg(String type, String msg) {
		msg(type, new StringBuffer(msg));
	}

	private static void msg(String type, StringBuffer msgBuf) {
		msg(type, msgBuf, null);
	}

	private static void msg(String type, String msg, Throwable e) {
		msg(type, new StringBuffer(msg), e);
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat(".yyyy-MM-dd");

	public static String getDayLogPath(String logFilePath) {
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

	private static void msg(String type, StringBuffer msgBuf,Throwable e,
			boolean isDateSpe) {
		StringBuffer sb = new StringBuffer();
		long nowTime = System.currentTimeMillis();
		sb.append(CalendarUtil.getLocalDateTime()).append(" ");
		sb.append(nowTime - beginTime).append("[")
		 .append(Thread.currentThread().getId())
				.append("]").append(" - ").append(type).append(": ").append(
						msgBuf);

		PrintStream ps = null;
		if (target == Logger.TARGET_SIMPLEFILE)
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
	private static void msg(String type, StringBuffer msgBuf, Throwable e) {
		msg(type, msgBuf, e, isLogDateSep);
	}

	public static int getLevel() {
		return level;
	}

	public static void info(String string, Throwable e) {
		info(new StringBuffer(string), e);
	}

	public static void main(String[] args) {
		Logger.setTarget(Logger.TARGET_SIMPLEFILE);
		Logger.setLogFilePath("/tmp/test");
		Logger.info("1");
		for (int i = 0; i < 100; i++) {
			Logger.info("" + i);
		}

	}

	public static String getLevelString() {
		return levelString;
	}

}
