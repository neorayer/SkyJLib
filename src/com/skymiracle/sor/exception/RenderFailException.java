package com.skymiracle.sor.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.templates.TemplateHelper;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class RenderFailException extends SorException {

	private static final long serialVersionUID = 1L;

	private String reason;

	public RenderFailException(String reason) {
		this.reason = reason;
	}

	@Override
	public void outToResponse(SorRequest request, SorResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding(SorFilter.encoding);
		response.setContentType("text/html");

		Map<String, String> root = new HashMap<String, String>();
		root.put("reason", reason);

		try {
			Template t = TemplateHelper.getTemplateOfRenderFailException();
			t.process(root, response.getWriter());
		} catch (TemplateException e) {
			response.getWriter().write(reason);
		}

	}

	@Override
	public String getReason() {
		return reason;
	}

}
