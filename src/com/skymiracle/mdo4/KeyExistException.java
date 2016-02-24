package com.skymiracle.mdo4;

public class KeyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5469400638214298928L;

	public KeyExistException() {
		super();
	}

	public KeyExistException(String message) {
		super(message);
	}

	public KeyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyExistException(Throwable cause) {
		super(cause);
	}
}
