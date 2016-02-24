package com.skymiracle.sor.render;

import java.io.IOException;

import javax.servlet.ServletException;

import com.skymiracle.json.DNAJson;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;

public class JsRenderer extends Renderer {

	@Override
	public void render() throws IOException, Exception {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		String s = actResult.getJs();
		response.getWriter().print("<script language='javascript'>" + s + "</script>");
	}

	@Override
	public void render(Exception e) throws Exception {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		response.getWriter().print("<script language='javascript'>alert('"+e.getMessage()+"');</script>");
	}

	@Override
	public void render(AppException e) throws IOException, ServletException {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		response.getWriter().print("<script language='javascript'>alert('"+e.getMessage()+"');</script>");
	}

}
