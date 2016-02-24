package com.skymiracle.sor.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.sor.exception.RenderFailException;
import com.skymiracle.sor.route.Route;
import com.skymiracle.sor.controller.WebController;

public abstract class Renderer {

	protected SorRequest request;

	protected SorResponse response;

	protected final ActResult actResult;

	private WebController<?> controller;

	public Renderer() {
		actResult = ActResult.get();
	}

	public static Map<String, Class<? extends Renderer>> renderersMap = new HashMap<String, Class<? extends Renderer>>();
	static {
		renderersMap.put("json", JsonRenderer.class);
		renderersMap.put("html", JspRenderer.class);
		renderersMap.put("sxml", SxmlRenderer.class);
		renderersMap.put("down", FileRenderer.class);
		renderersMap.put("xml", XmlRenderer.class);
		renderersMap.put("authImage", AuthImageRenderer.class);
		renderersMap.put("img", ImageRenderer.class);
		renderersMap.put("hessian", HessianRenderer.class);
		renderersMap.put("sjs", JsRenderer.class);
	}

	public abstract void render() throws IOException, Exception;

	public abstract void render(Exception e) throws Exception;

	public abstract void render(AppException e) throws IOException,
			ServletException;

	public static Renderer create(Route route, SorRequest request,
			SorResponse response) throws RenderFailException {
		Class<? extends Renderer> rendererClass = renderersMap.get(route
				.getFormat());
		if (rendererClass == null)
			throw new RenderFailException("无法找到渲染器：" + route.getFormat());

		try {
			Renderer renderer = rendererClass.newInstance();
			renderer.request = request;
			renderer.response = response;
			response.setCharacterEncoding("UTF-8");

			if (renderer.isPageRenderer()) {
				PageRenderer htmlRenderer = (PageRenderer) renderer;
				htmlRenderer.setNs(route.getActor());
				htmlRenderer.setForder(route.getResource());
				htmlRenderer.setPage(route.getOperate());
				htmlRenderer.setNeedLayout(!request.hasLayout());
				htmlRenderer.setLayoutName(route.getResource());
				htmlRenderer.setTheme("");
			}

			return renderer;
		} catch (Exception e) {
			throw new RenderFailException("渲染器初始化失败");
		}
	}

	public boolean isPageRenderer() {
		return PageRenderer.class.isAssignableFrom(this.getClass());
	}

	public WebController<?> getController() {
		return controller;
	}

	public void setController(WebController<?> controller) {
		this.controller = controller;
	}

	public static boolean canRenderPath(String appPath) {
		int index = appPath.lastIndexOf('.');
		if (index == -1)
			return true;

		return renderersMap.containsKey(appPath.substring(index + 1));
	}

}
