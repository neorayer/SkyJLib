package com.skymiracle.sor.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.templates.TemplateHelper;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class NoSuchControllerMethodException extends SorException {

	private static final long serialVersionUID = 1L;

	private String controllerName;

	private String actionName;

	public NoSuchControllerMethodException(String controllerName,
			String actionName) {
		this.controllerName = controllerName;
		this.actionName = actionName;
	}

	@Override
	public void outToResponse(SorRequest request, SorResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding(SorFilter.encoding);
		response.setContentType("text/html");

		Map<String, String> root = new HashMap<String, String>();
		root.put("reason", getReason());

		try {
			Template t = TemplateHelper.getTemplateOfNoSuchControllerMethodException();
			t.process(root, response.getWriter());
		} catch (TemplateException e) {
			response.getWriter().write(getReason());
		}

	}

	@Override
	public String getReason() {
		return "你访问的控制器类不存在该方法! " + controllerName + "." + actionName + "()";
	}

}
