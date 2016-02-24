package com.skymiracle.mdo4;

public class NullKeyException extends Exception {

	private static final long serialVersionUID = -454934495604947126L;

	public NullKeyException() {
		super();
	}

	public NullKeyException(String message) {
		super(message);
	}

	public NullKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullKeyException(Throwable cause) {
		super(cause);
	}
}
