/*
 * Created by dnoakes on 13-Nov-2002 18:10:23 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata;

/**
 * 
 */
public class MetadataException extends CompoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7268369881189494786L;

	public MetadataException(String msg) {
		super(msg);
	}

	public MetadataException(Throwable exception) {
		super(exception);
	}

	public MetadataException(String msg, Throwable innerException) {
		super(msg, innerException);
	}
}
