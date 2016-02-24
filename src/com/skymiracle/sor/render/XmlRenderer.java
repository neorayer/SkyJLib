package com.skymiracle.sor.render;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.exception.AppException;

public class XmlRenderer extends Renderer {

	@Override
	public void render() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		response.setContentType("text/xml;charset=" + request.getCharacterEncoding());
		response.getWriter().println(actResult.getXmlText());
	}

	@Override
	public void render(Exception e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}

	@Override
	public void render(AppException e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}

}
