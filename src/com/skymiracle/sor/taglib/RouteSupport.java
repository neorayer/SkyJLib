package com.skymiracle.sor.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class RouteSupport  extends TagSupport {
	
	protected String route;

	protected String actor;

	protected String module;

	protected String resource;

	//protected String resourceId;
	
	protected String operate;

	protected String format;

	protected String way;
	
	public void setRoute(String route) {
		this.route = route;
		System.out.println("route:" + route);
	}
	
	public void setActor(String actor) {
		this.actor = actor;
		System.out.println("actor:" + actor);
	}
	
	public void setModule(String module) {
		this.module = module;
		System.out.println("module:" + module);
	}
	
	public void setResource(String resource) {
		this.resource = resource;
		System.out.println("resource:" + resource);
	}
	
	public void setOperate(String operate) {
		this.operate = operate;
		System.out.println("operate:" + operate);
	}
	
	public void setFormat(String format) {
		this.format = format;
		System.out.println("format:" + format);
	}
	
	public void setWay(String way) {
		this.way = way;
		System.out.println("way:" + way);
	}
	
	
	@Override
	public int doStartTag() throws JspException {
//		String url ="" + actor + "/" + module + "/" + resource + "/" + resource + "." + operate + "." + format + "." + way;
//		try {
//			pageContext.getOut().write(url);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		System.out.println("doEndTag");
		return super.doEndTag();
	}
	
	
	
}
