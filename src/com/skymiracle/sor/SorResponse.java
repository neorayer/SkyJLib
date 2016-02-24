package com.skymiracle.sor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SorResponse extends HttpServletResponseWrapper {

	public SorResponse(HttpServletResponse response) {
		super(response);
	}
}
//