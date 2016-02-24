package com.skymiracle.sor.route;

import java.lang.reflect.Method;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.controller.WebController;
import com.skymiracle.sor.exception.ControllerBuildException;
import com.skymiracle.sor.exception.NoSuchControllerMethodException;
import com.skymiracle.sor.exception.RouteNotFindException;
import com.skymiracle.util.StringUtils;

public class Route {

	// 应用包名
	public static String packageName;

	// 主机
	private String host;

	// 请求路径
	private String path;

	// 角色
	private String actor;

	// 资源
	private String resource;

	// 资源ID
	private String rid;

	// 操作
	private String operate;

	// 返回数据格式
	// URL中format可以为NULL
	private String format;

	// 是否ajax请求
	private boolean isAjax;

	public Route() {
	}

	public static Route parse(String host, String appPath, boolean isAjax)
			throws RouteNotFindException {
		Route route = new Route();
		route.host = host;
		route.path = appPath;
		route.isAjax = isAjax;

		// 去掉“/”
		if (appPath.startsWith("/")) {
			appPath = appPath.substring(1);
		}

		// format
		int index = appPath.lastIndexOf('.');
		if (index > -1) {
			route.format = appPath.substring(index + 1);
			appPath = appPath.substring(0, index);
		}

		// 不能在有“.”
		if (appPath.lastIndexOf('.') > -1) {
			throw new RouteNotFindException(route);
		}

		String[] ss = appPath.split("/+");
		
		if (ss.length == 1) {
			if(ss[0].length() == 0) {
				route.actor = "u";
				route.resource = "portal";
				route.operate = "index";
			}
			else if (ss[0].length() == 1) {
				route.actor = ss[0];
				route.resource = "portal";
				route.operate = "index";
			} else {
				route.actor = "u";
				route.resource = "portal";
				route.operate = ss[0];
			}
		} else if (ss.length == 2) {
			// actor，只能为一个字母
			if (ss[0].length() == 1) {
				route.actor = ss[0];
				route.resource = "portal";
				route.operate = ss[1];
			} else {
				// TODO actor可以包含在二级域名中
				route.actor = "u";
				route.resource = ss[0];
				route.operate = ss[1];
			}

		} else if (ss.length == 3) {
			// actor，只能为一个字母
			if (ss[0].length() == 1) {
				route.actor = ss[0];
				route.resource = ss[1];
				route.operate = ss[2];
			} else {
				route.actor = "u";
				route.resource = ss[0];
				route.rid = ss[1];
				route.operate = ss[2];
			}
		} else {
			throw new RouteNotFindException(route);
		}

		// 设置默认渲染格式
		if (route.format == null) {
			if (route.isAjax)
				route.format = "json";
			else
				route.format = "html";
		}

		return route;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		try {
			buf.append("\n\t host: " + this.host);
			buf.append("\n\t path: " + this.path);
			buf.append("\n\t actor: " + this.actor);
			buf.append("\n\t resource: " + this.resource);
			buf.append("\n\t rid: " + this.rid);
			buf.append("\n\t operate: " + this.operate);
			buf.append("\n\t format: " + this.format);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return buf.toString();

	}

	@SuppressWarnings("unchecked")
	public WebController<?> recognize(SorRequest request, SorResponse response)
			throws ControllerBuildException, NoSuchControllerMethodException {
		// 控制器完整类名
		StringBuffer buffer = new StringBuffer(packageName);
		buffer.append(".controllers.");
		buffer.append(actor);
		buffer.append(".");
		buffer.append(StringUtils.toFirstCap(resource)).append("Ctr");

		String ctrClassName = buffer.toString();
		WebController<?> controller = null;
		try {
			Class<? extends WebController<?>> ctrClass = (Class<? extends WebController<?>>) Class
					.forName(ctrClassName);
			controller = ctrClass.newInstance();
			controller.init(request, response, this);
		} catch (Exception e) {
			Logger.warn("", e);
			throw new ControllerBuildException(ctrClassName);
		}

		Method method = null;
		try {
			method = controller.getClass().getMethod(operate, new Class[0]);
			controller.setMethod(method);
		} catch (Exception e) {
			Logger.warn("[INIT METHOD]" + operate, e);
			throw new NoSuchControllerMethodException(controller.getClass()
					.getName(), operate);
		}

		return controller;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public String getActor() {
		return actor;
	}

	public String getResource() {
		return resource;
	}

	public String getRid() {
		return rid;
	}

	public String getOperate() {
		return operate;
	}

	public String getFormat() {
		return format;
	}

	public boolean isAjax() {
		return isAjax;
	}

}
