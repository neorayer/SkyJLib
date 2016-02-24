package com.skymiracle.logger.analyze;

public interface MessageFormat {
	public String format();

	public void parse(String input) throws UnSupportFormatMessage;
}
