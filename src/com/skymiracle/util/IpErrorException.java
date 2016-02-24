package com.skymiracle.util;

public class IpErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3249823533007425876L;

	public IpErrorException(String ip) {
		super("IP=" + ip);
	}
}
