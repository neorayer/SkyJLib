package com.skymiracle.skyUpload;

public class SkyUploadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5123778229936891776L;

	SkyUploadException(String s) {
		super(s);
	}

	SkyUploadException(String s, Exception e) {
		super(s, e);
	}
}
