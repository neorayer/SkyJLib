package com.skymiracle.server.udpServer;

public class ErrorCommanderClassException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6688371486349358328L;

	public ErrorCommanderClassException(Class clazz) {
		super(clazz.getSimpleName());
	}
}
