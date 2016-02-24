package com.skymiracle.client.tcpClient.smtpClient;

public  class SmtpSendException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3197535284222800377L;

	private int type;
	
	public SmtpSendException(int type , Exception e) {
		super(e);
		this.type = type;
	}

	public SmtpSendException(int type , String msg) {
		super(msg);
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
}
