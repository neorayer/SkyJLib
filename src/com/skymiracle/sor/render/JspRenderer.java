package com.skymiracle.sor.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.controller.WebController;
import com.skymiracle.sor.exception.AppException;

public class JspRenderer extends PageRenderer {
	@Override
	public void render() throws IOException, Exception {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		putActResultToRequest();

		String path = getPagePath();
		if (isNeedLayout()) {
			String layout = getLayoutPath();
			request.setPL(path);
			request.getRequestDispatcher(layout).forward(request, response);
		} else {
			request.getRequestDispatcher(path).include(request, response);
		}
		Logger.debug("[FWD] " + path);
	}

	public String getPagePath() {
		return new StringBuffer().append("/").append(this.ns).append("/")
				.append(this.forder).append("/").append(this.page).append(
						".jsp").toString();
	}

	public String getLayoutPath() {
		return new StringBuffer().append("/").append(this.ns).append("/")
				.append(this.layoutName).append("/").append(
						this.getThemeLayout()).append(".jsp").toString();
	}

	public String getThemeLayout() {
		return "".equals(this.theme) ? "layout" : this.theme + "_layout";
	}

	@Override
	public void render(Exception e) throws Exception {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		PrintWriter w = response.getWriter();
		w.println("<h1>系统内部错误</h1>");
		w.println("<textarea style='width:100%;height:200px'>");
		e.printStackTrace(w);
		w.println("</textarea>");

	}

	protected void renderTip() throws IOException {
		PrintWriter w = response.getWriter();
		w.println("<script language='javascript'>");
		w.println("var exDom = document.getElementById('SOR_EXCEPTION');");
		w.println("if (!exDom) {");
		w.println("	alert('您的页面缺少 SOR_EXCEPTION')");
		w
				.println("	document.write('<textarea><DIV id=\"SOR_EXCEPTION\" style=\"color:red\"><c:out value=\"${REASON}\" /></DIV></textarea>')");
		w.println("}");
		w.println("</script>");

	}

	@Override
	public void render(AppException appEx) throws IOException, ServletException {
		response.setContentType("text/html;charset=" + request.getCharacterEncoding());
		request.setAttribute("REASON", appEx.getMessage());
		try {
			WebController<?> controller = getController();
			controller.setIs_get(true);
			controller.process().render();
		} catch (Throwable e) {
			Logger.error(e.getMessage());
		}

	}

}
