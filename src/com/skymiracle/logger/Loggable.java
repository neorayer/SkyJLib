package com.skymiracle.logger;

public interface Loggable {

	public void warn(String s, Exception e);

	public void warn(String s);

	public void error(String s, Exception e);

	public void error(String s);

	public void info(StringBuffer sb);

	public void info(String s);

	public void fatalError(StringBuffer sb);

	public void fatalError(StringBuffer sb, Exception e);

	public void fatalError(String s, Exception e);

	public void info(StringBuffer sb, Exception e);

	public void debug(String s);

	public void detail(String s);

	public void debug(StringBuffer sb);

}