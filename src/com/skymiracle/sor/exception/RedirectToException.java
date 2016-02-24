package com.skymiracle.sor.exception;

import java.io.IOException;
import java.util.regex.Pattern;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;

public class RedirectToException extends RuntimeException {

	private static Pattern siteCompile = Pattern
			.compile("^[a-zA-z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?");

	private String url;

	public RedirectToException(String url) {
		super("RedirectTo");
		this.url = url.startsWith("/") ? url: "/" + url;
	}

	private static final long serialVersionUID = 1L;

	public boolean isSite(String path) {
		return siteCompile.matcher(path).find();
	}

	public void redirectTo(SorRequest request, SorResponse response)
			throws IOException {
		if(url == null)
			return;
		
		if (!isSite(url))
			this.url = request.getContextPath() + url;

		response.sendRedirect(url);
		Logger.debug("[RDT] " + url);
	}

}
