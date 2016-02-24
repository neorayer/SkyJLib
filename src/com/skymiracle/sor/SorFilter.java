package com.skymiracle.sor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.controller.WebController;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.sor.exception.NoSessionException;
import com.skymiracle.sor.exception.RedirectToException;
import com.skymiracle.sor.exception.RenderFailException;
import com.skymiracle.sor.exception.RouteNotFindException;
import com.skymiracle.sor.exception.SorException;
import com.skymiracle.sor.render.Renderer;
import com.skymiracle.sor.route.Route;
import com.skymiracle.sor.templates.TemplateHelper;

public class SorFilter implements Filter {

	public static WebApplicationContext context = null;

	public static String encoding = "UTF-8";

	public void init(FilterConfig conf) throws ServletException {
		Logger.info("SorFilter init.");

		String loggerLevel = conf.getInitParameter("loggerLevel");
		if (loggerLevel != null)
			Logger.setLevel(loggerLevel);
		else
			Logger.setLevel(Logger.LEVEL_DEBUG);

		String encodingStr = conf.getInitParameter("encoding");
		if (encodingStr != null)
			encoding = encodingStr;

		String packageName = conf.getInitParameter("AppHome");
		if (packageName == null || packageName.equals(""))
			throw new RuntimeException("AppHome Not Defined!");

		Route.packageName = packageName;

		// 项目的物理路径
		SorRequest.Project_Real_Path = conf.getServletContext()
				.getRealPath("/");
		TemplateHelper.newInstatnce();

		context = WebApplicationContextUtils.getWebApplicationContext(conf
				.getServletContext());
		Logger.debug("Spring WebApplicationContext:" + context);
	}

	public static WebApplicationContext getContext() {
		return context;
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		SorRequest request = new SorRequest((HttpServletRequest) req);
		SorResponse response = new SorResponse((HttpServletResponse) resp);

		String appPath = request.getAppPath();
		if (!Renderer.canRenderPath(appPath)
				|| TemplateHelper.isStaticFile(appPath)
				|| appPath.equals("/_ws") || appPath.indexOf("_ws/") > 0) {
			java.util.Date date = new java.util.Date();
			response.setDateHeader("Last-Modified", date.getTime());
			response.setDateHeader("Expires", date.getTime() + 10000);
			chain.doFilter(request, response);
			return;
		}

		request.setCharacterEncoding(encoding);

		// 记录访问请求的URL日志
		if (request.getAttribute("PROC_FIRST") == null) {
			String remoteAddr = request.getRemoteAddr();
			String method = request.getMethod();
			request.setAttribute("PROC_FIRST", System.currentTimeMillis());
			Logger.info("[REQ] ["
					+ ("get".equalsIgnoreCase(method) ? "GET" : "POST") + "] ["
					+ remoteAddr + "] " + request.getRequestURL());
		}

		try {
			handleRequest(appPath, request, response);
		} finally {
			// request.clear();
		}
	}

	private void handleRequest(String appPath, final SorRequest request,
			final SorResponse response) throws IOException, ServletException {
		// 路由
		Route route = null;
		try {
			route = Route.parse(request.getHeader("host"), appPath, request
					.isXmlHttpRequest());
		} catch (RouteNotFindException e) {
			e.outToResponse(request, response);
			return;
		}

		// 渲染器
		Renderer renderer = null;
		try {
			renderer = Renderer.create(route, request, response);
		} catch (RenderFailException e) {
			e.outToResponse(request, response);
			return;
		}

		// 控制器
		WebController<?> controller = null;
		try {
			controller = route.recognize(request, response);
			controller.setRenderer(renderer);
		} catch (SorException e) {
			e.outToResponse(request, response);
			return;
		}

		try {
			// 执行并渲染结果
			try {
				controller.process().render();
			} catch (NoSessionException e) {
				try {
					controller.dealWithNoSession();
				} catch (RedirectToException e1) {
					e1.redirectTo(request, response);
				}
			} catch (RedirectToException e) {
				e.redirectTo(request, response);
			} catch (AppException e) {
				renderer.render(e);
			} catch (Throwable e) {
				throw new ServletException(e);
			}
		} finally {
			ActResult.clear();
			request.clear();
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
}
