package com.skymiracle.sor.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.controller.WebController;
import com.skymiracle.sor.exception.RenderFailException;
import com.skymiracle.sor.exception.RouteNotFindException;
import com.skymiracle.sor.exception.SorException;
import com.skymiracle.sor.render.PageRenderer;
import com.skymiracle.sor.render.Renderer;
import com.skymiracle.sor.route.Route;
import com.skymiracle.sor.templates.TemplateHelper;

public class SorImportTag extends ImportSupport {

	// *********************************************************************
	// 'Private' state (implementation details)

	private static final long serialVersionUID = 1L;

	private String url_; 
	
	private SorRequest request;

	private SorResponse response;

	// *********************************************************************
	// Constructor

	/**
	 * Constructs a new ImportTag. As with TagSupport, subclasses should not
	 * provide other constructors and are expected to call the superclass
	 * constructor
	 */
	public SorImportTag() {
		super();
		init();
	}

	// *********************************************************************
	// Tag logic

	// evaluates expression and chains to parent
	public int doStartTag() throws JspException {

		// evaluate any expressions we were passed, once per invocation
		evaluateExpressions();

		// process
		try {
			process();
		} catch (IOException e) {
			throw new JspException(e);
		}

		// chain to the parent implementation
		return super.doStartTag();
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		super.release();
		init();
	}

	// *********************************************************************
	// Accessor methods

	// for EL-based attribute
	public void setUrl(String url_) {
		this.url_ = url_;
	}

	// *********************************************************************
	// Private (utility) methods

	// (re)initializes state (during release() or construction)
	private void init() {
		// null implies "no expression"
		url_ = null;
	}

	/* Evaluates expressions as necessary */
	private void evaluateExpressions() throws JspException {
		/*
		 * Note: we don't check for type mismatches here; we assume the
		 * expression evaluator will return the expected type (by virtue of
		 * knowledge we give it about what that type is). A ClassCastException
		 * here is truly unexpected, so we let it propagate up.
		 */

		url = (String) ExpressionUtil.evalNotNull("import", "url", url_,
				String.class, this, pageContext);
		if (url == null || url.equals(""))
			throw new NullAttributeException("import", "url");
	}

	private void process() throws JspException, IOException {
		initContext();
		// 把相对路径，转换为绝对路径
		String servletPath = request.getServletPath();
		if(!servletPath.startsWith("/")) {
			servletPath = this.getRelativeRootPath() + "/" + servletPath;
		}
		
		if(!Renderer.canRenderPath(servletPath) || TemplateHelper.isStaticFile(servletPath)) {
			return;
		}


		// 路由
		Route route = null; 
		try {
			route = Route.parse(request.getHeader("host"), servletPath, false);
		} catch (RouteNotFindException e) {
			return;
		}

		// 渲染器
		Renderer renderer = null;
		try {
			renderer = Renderer.create(route, request, response);
		} catch (RenderFailException e) {
			return;
		}

		if (!renderer.isPageRenderer())
			return;

		// 控制器
		WebController<?> controller = null;
		try {
			controller = route.recognize(request, response);
			controller.setRenderer(renderer);
		} catch (SorException e) {
			return;
		}

		try {
			PageRenderer pageRender = (PageRenderer)controller.process();
			pageRender.putActResultToRequest();
			this.url = pageRender.getPagePath();
		} catch (Throwable e) {
			Logger.error("", e);
			return;
		}
	}

	private void initContext() {
		request = new ImportTagRequest((HttpServletRequest) this.pageContext
				.getRequest(), this.url);
		response = new SorResponse((HttpServletResponse) this.pageContext
				.getResponse());
	}
	
	private String getRelativeRootPath() {
		String s = pageContext.getPage().toString();
		s = s.substring("org.apache.jsp".length(), s.indexOf("@"));
		s = s.substring(0, s.lastIndexOf(".")).replace('.', '/');
		return s;
	}

	class ImportTagRequest extends SorRequest {

		private String uri;

		private String servletPath;

		private String queryString;

		private Map<String, String> paramMap = new HashMap<String, String>();

		public ImportTagRequest(HttpServletRequest request, String uri) {
			super(request);
			this.uri = uri;

			int qindex = uri.indexOf("?");
			this.servletPath = (qindex > -1 ? uri.substring(0, qindex) : uri);
			this.queryString = qindex > -1 ? uri.substring(qindex + 1) : null;
			if (this.queryString != null) {
				String[] pairs = this.queryString.split("&");
				for (String p : pairs) {
					String[] ss = p.split("=");
					if (ss.length == 2 && ss[0] != null && !ss[0].equals(""))
						paramMap.put(ss[0], ss[1]);
				}
			}
		}

		public String getRequestURI() {
			return uri;
		}

		public void setRequestURI(String uri) {
			this.uri = uri;
		}

		public String getServletPath() {
			return servletPath;
		}

		@Override
		public String getQueryString() {
			return queryString;
		}

//		@Override
//		public String get(String name) {
//			if (paramMap.get(name) != null)
//				return paramMap.get(name);
//			return super.get(name);
//		}

		@Override
		public String getParameter(String name) {
			System.out.println("getParameter: " + name);
			if (paramMap.get(name) != null)
				return paramMap.get(name);
			return super.getParameter(name);
		}
		
		
	}
}
