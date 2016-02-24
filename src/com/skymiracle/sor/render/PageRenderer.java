package com.skymiracle.sor.render;

import java.util.Map;

import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.exception.RedirectToException;

public abstract class PageRenderer extends Renderer {

	// 命名空间（对应Route的actor）
	protected String ns;

	// 文件夹（对应Route的resource）
	protected String forder;

	// 页面（对应Route的operate）
	protected String page;

	// 是否需要布局
	protected boolean needLayout;

	// 布局
	protected String layoutName;

	// 主题
	protected String theme;
	
	// 转向地址
	protected String redirectTo;

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public String getForder() {
		return forder;
	}

	public PageRenderer setForder(String forder) {
		this.forder = forder;
		return this;
	}

	public String getPage() {
		return page;
	}

	public PageRenderer setPage(String page) {
		this.page = page;
		return this;
	}

	public boolean isNeedLayout() {
		return needLayout;
	}

	public void setNeedLayout(boolean needLayout) {
		this.needLayout = needLayout;
	}

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = "DEFAULT_LAYOUT".equals(layoutName) ? this.forder
				: layoutName;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme == null ? "" : theme;
	}
	
	public abstract String getPagePath();

	public void redirectTo(String redirectTo) {
		// TODO 可以采用自身的路径，来更改相对路径。
		throw new RedirectToException(redirectTo);
	}
	
	public void putActResultToRequest() {
		// Set all data to request
		Map<String, Object> dataMap = actResult.getDataMap();
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
		request.setAttribute("dataColl", actResult.getDataColl());
		request.setAttribute("dataSingle", actResult.getDao());
		request.setAttribute("ACTOR", actResult.getActor());
		request.setAttribute("ACTOR_CLASS", actResult.getActorClassName());
		request.setAttribute("ProjectBase", request.getProjectBasePath());
	}

}
