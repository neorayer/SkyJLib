package com.skymiracle.mdo5;

import com.skymiracle.sor.exception.AppException;

public class NotEmptyException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1779279368147022462L;

	public NotEmptyException() {
		super("Not empty");
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
