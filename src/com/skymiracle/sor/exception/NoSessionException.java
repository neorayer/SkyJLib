package com.skymiracle.sor.exception;

public class NoSessionException extends Exception {

	private static final long serialVersionUID = -2666275847224266022L;

	private String sessionName;

	public NoSessionException(String sessionName) {
		super("timeout");
		this.sessionName = sessionName;
	}

	public String getSessionName() {
		return sessionName;
	}
	

}
