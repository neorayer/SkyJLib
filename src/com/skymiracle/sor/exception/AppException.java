package com.skymiracle.sor.exception;

import java.util.LinkedList;
import java.util.List;

public class AppException extends Exception{


	private static final long serialVersionUID = -7670250297775398043L;
	
	private List<String> errors = new LinkedList<String>();

	public AppException(String message ) {
		super(message);
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(Throwable cause) {
		super(cause);
	}
	
	public void addError(String error) {
		this.errors.add(error);
	}
}
