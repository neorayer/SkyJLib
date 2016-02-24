package com.skymiracle.sor.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;

public abstract class SorException extends Exception {
	private static final long serialVersionUID = 1L;

	public abstract void outToResponse(SorRequest request, SorResponse response)
			throws IOException, ServletException;

	public abstract String getReason();
}
