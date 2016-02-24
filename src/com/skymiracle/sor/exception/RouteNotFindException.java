package com.skymiracle.sor.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.route.Route;
import com.skymiracle.sor.templates.TemplateHelper;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class RouteNotFindException extends SorException {

	private String reason;

	private static final long serialVersionUID = 1L;

	private Route route;

	public RouteNotFindException(Route route) {
		this.route = route;
		this.reason = "您访问的地址不存在，请确认您输入的URL地址! path = " + route.getPath();
	}

	@Override
	public void outToResponse(SorRequest request, SorResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding(SorFilter.encoding);
		response.setContentType("text/html");

		Map<String, String> root = new HashMap<String, String>();
		root.put("path", route.getPath());
		root.put("reason", reason);

		try {
			Template t = TemplateHelper.getTemplateOfRouteNotFindException();
			t.process(root, response.getWriter());
		} catch (TemplateException e) {
			response.getWriter().write(reason);
		}
	}

	@Override
	public String getReason() {
		return this.reason;
	}
}
