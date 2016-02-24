package com.skymiracle.mdo4;

public class NotEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1779279368147022462L;

	public NotEmptyException() {
		super();
	}

	public NotEmptyException(String message) {
		super(message);
	}

	public NotEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotEmptyException(Throwable cause) {
		super(cause);
	}
}
