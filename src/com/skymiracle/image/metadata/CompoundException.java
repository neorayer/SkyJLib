package com.skymiracle.image.metadata;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Represents a compound exception, as modelled in JDK 1.4, but unavailable in
 * previous versions. This class allows support of these previous JDK versions.
 */
public class CompoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9042024655887876930L;
	private final Throwable _innnerException;

	public CompoundException(String msg) {
		this(msg, null);
	}

	public CompoundException(Throwable exception) {
		this(null, exception);
	}

	public CompoundException(String msg, Throwable innerException) {
		super(msg);
		this._innnerException = innerException;
	}

	public Throwable getInnerException() {
		return this._innnerException;
	}

	@Override
	public String toString() {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append(super.toString());
		if (this._innnerException != null) {
			sbuffer.append("\n");
			sbuffer.append("--- inner exception ---");
			sbuffer.append("\n");
			sbuffer.append(this._innnerException.toString());
		}
		return sbuffer.toString();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		if (this._innnerException != null) {
			s.println("--- inner exception ---");
			this._innnerException.printStackTrace(s);
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
		if (this._innnerException != null) {
			s.println("--- inner exception ---");
			this._innnerException.printStackTrace(s);
		}
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
		if (this._innnerException != null) {
			System.err.println("--- inner exception ---");
			this._innnerException.printStackTrace();
		}
	}
}
