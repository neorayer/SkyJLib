package com.skymiracle.mdo5;

public class MdoBuildException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5669744052451710243L;

	public MdoBuildException() {
		super();
	}

	public MdoBuildException(String message) {
		super(message);
	}

	public MdoBuildException(String message, Throwable cause) {
		super(message, cause);
	}

	public MdoBuildException(Throwable cause) {
		super(cause);
	}
}
