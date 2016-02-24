package com.skymiracle.mdo4;

public class KeyNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3580376185509197481L;

	public KeyNotExistException() {
		super();
	}

	public KeyNotExistException(String message) {
		super(message);
	}

	public KeyNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyNotExistException(Throwable cause) {
		super(cause);
	}
}
