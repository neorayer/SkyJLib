package com.skymiracle.sor.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.skymiracle.http.HttpUploader;
import com.skymiracle.http.HttpUploader.ProcListener;
import com.skymiracle.http.HttpUploader.TempUpFile;
import com.skymiracle.image.SkyImage;
import com.skymiracle.image.SkyImageImpl;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo5.MList;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.MdoMap;
import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.annotation.Layout;
import com.skymiracle.sor.annotation.NoSessioned;
import com.skymiracle.sor.annotation.Sessioned;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.sor.exception.ControllerInvokeException;
import com.skymiracle.sor.exception.NoSessionException;
import com.skymiracle.sor.exception.RedirectToException;
import com.skymiracle.sor.render.PageRenderer;
import com.skymiracle.sor.render.Renderer;
import com.skymiracle.sor.route.Route;
import com.skymiracle.validate.ValidateException;

public abstract class WebController<ActorType> {

	protected SorRequest request;

	protected SorResponse response;

	protected HttpSession session;

	protected Renderer renderer;

	protected final ActResult r;

	private Method method;

	protected boolean is_get;

	protected boolean is_xml_http_request;

	private String actorSessionName;

	protected String actorId;

	protected ActorType actor;

	public WebController() {
		r = ActResult.get();
	}

	public void init(SorRequest request, SorResponse response, Route route) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
		this.actorSessionName = getClass().getPackage().getName();
		this.is_get = request.isGet();
		this.is_xml_http_request = request.isXmlHttpRequest() || "json".equals(route.getFormat());
	}

	public void setMethod(Method method) {
		this.method = method;
	}


	@SuppressWarnings("unchecked")
	public void initActor() {
		this.actorId = getActorId();
		if (this.actorId != null) { // HTTP Cookie 中 包含actorId
			// 先从request thread local 中取 
			actor =	(ActorType)request.getThreadLocalActor();
			if(actor != null)
				return;
			
			try {
				actor = getActorFromId();
				if (actor == null) {
					clearActorId();
					return;
				}
				
				request.setThreadLocalActor(actor);
			} catch (Exception e) {
				Logger.warn("", e);
				clearActorId();
			}
		}
	}

	public String getActorId() {

		String actorId = (String) getSession(actorSessionName);
		if (actorId == null) {
			Cookie cookie = request.findCookie(actorSessionName);
			if (cookie != null)
				actorId = cookie.getValue();
		}

		if (actorId == null)
			return null;

		try {
			return URLDecoder.decode(actorId, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}

	public void setIs_get(boolean is_get) {
		this.is_get = is_get;
	}

	public boolean isIs_xml_http_request() {
		return is_xml_http_request;
	}

	public void setIs_xml_http_request(boolean is_xml_http_request) {
		this.is_xml_http_request = is_xml_http_request;
	}

	public boolean isIs_get() {
		return is_get;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
		this.renderer.setController(this);
	}

	public Renderer process() throws NoSessionException, Throwable {
		// Session检查
		try {
			sessionCheck();
		} catch (NoSessionException e) {
			throw e;
		}

	
		long beginTime = System.currentTimeMillis();
		try {
			// 文件上传
			try {
				request.doUpload();
			} catch (Exception e) {
				Logger.debug("", e);
				throw new AppException(e.getMessage());
			}

			// 控制器前置调用
			invokeBefore();

			// 调用控制类方法
			method.invoke(this, new Object[0]);

			// 控制器后置调用
			invokeAfter();

			// 设置布局和主题
			if (renderer.isPageRenderer()) {
				PageRenderer pageRenderer = (PageRenderer) renderer;
				String layoutName = getLayoutName();
				boolean isNeedLayout = !"NOT".equals(layoutName)
						&& !request.hasLayout();
				pageRenderer.setNeedLayout(isNeedLayout);
				pageRenderer.setLayoutName(layoutName);
				pageRenderer.setTheme(getTheme());
			}

			// 将角色写入结果ActResult
			putActorToResult();

			return renderer;
		} catch (NoSessionException e) {
			throw e;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			// 非应用异常，输入错误日志
			if (!(cause instanceof AppException) && !(cause instanceof RedirectToException))
				Logger.error("", e);

			throw e.getCause();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new ControllerInvokeException(method.getName(), e);
		} finally {
			long spent = System.currentTimeMillis() - beginTime;
			String s = "[IVK] [" + spent + "ms] "
					+ this.getClass().getSimpleName() + "." + method.getName()
					+ "()";
			Logger.debug(s);
		}
	}

	private String getLayoutName() {
		Layout layout = method.getAnnotation(Layout.class);
		if (layout != null && !"".equals(layout.value()))
			return layout.value();
		return "DEFAULT_LAYOUT";
	}
	
	protected PageRenderer page() {
		return (PageRenderer) renderer;
	}

	private void invokeAfter() {
		// TODO Auto-generated method stub

	}

	public Object getSession(String name) {
		return session.getAttribute(name);
	}

	public void delSession(String name) {
		session.removeAttribute(name);
	}
	
	protected void putSession(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void delCookie(String cookieName) {
		response.addCookie(request.createCookie(cookieName, null, 0));
	}

	private void sessionCheck() throws NoSessionException {
		initActor();

		// 判断methodName是否需要sessioncheck
		Sessioned typeSessioned = getClass().getAnnotation(Sessioned.class);
		Sessioned methodSessioned = method.getAnnotation(Sessioned.class);
		NoSessioned methodNoSessioned = method.getAnnotation(NoSessioned.class);

		// 任何标注@NoSessioned的方法都不需要Session检查
		if (methodNoSessioned != null)
			return;

		// 全部method都需要Session
		if (typeSessioned != null) {
			if (this.actorId == null && this.actor == null)
				throw new NoSessionException(actorSessionName);
		} else {
			if (methodSessioned != null)
				if (this.actorId == null && this.actor == null)
					throw new NoSessionException(actorSessionName);
		}
	}

	public abstract ActorType getActorFromId() throws AppException, Exception;

	public void invokeBefore() throws AppException, Exception {
	}

	private void putActorToResult() {
		if (actor == null)
			return;

		r.setActor(actor);
	}

	public void actorLogin(String actorId) {
		try {
			actorId = (actorId == null ? null : URLEncoder.encode(actorId,
					"utf-8"));
		} catch (UnsupportedEncodingException e) {
		}
		saveSessionVar(actorSessionName, actorId);
	}
	
	public void saveSessionVar(String key, String value) {
		putSession(key, value);
		//response.addCookie(request.createCookie(key, value, -1));
	}
	
	public void delSessionVar(String key) {
		Logger.debug("delSessionVar: " + key);
		delSession(key);
		//response.addCookie(request.createCookie(key, null, 0));
	}

	public <T extends Dao> T $(Class<T> daoClass) throws ValidateException,
			Exception {
		return request.getBean(daoClass);
	}

	public <T extends Mdo<?>> T $M(Class<T> mdoClass) throws AppException,
			Exception {
		return request.getMdo(mdoClass);
	}

	protected <T> List<T> $$(Class<T> beanClass, String... field)
			throws ValidateException, Exception {
		return request.getBeans(beanClass, field);
	}

	protected <T extends Mdo<T>> MList<T> $$M(Class<T> beanClass,
			String... field) throws ValidateException, Exception {
		return request.getMdos(beanClass, field);
	}

	protected DaoAttrSet $D(Class<? extends Dao> daoClass)
			throws UnsupportedEncodingException {
		return request.getDaoAttrSet(daoClass);
	}

	protected MdoMap $MM(Class<? extends Mdo<?>> mdoClass)
			throws UnsupportedEncodingException {
		return request.getMdoMap(mdoClass);
	}

	protected String $(String name) {
		return request.get(name);
	}

	protected String $(String name, String defValue) {
		String v = $(name);
		return v == null ? defValue : v;
	}

	protected String[] $$s(String name, String sepRegex) {
		String v = $(name);
		if (v == null)
			return new String[0];
		return v.split(sepRegex);
	}

	protected boolean $b(String name) {
		String v = $(name);
		return Boolean.parseBoolean(v);
	}

	protected boolean $b(String name, boolean defaultValue) {
		String v = $(name);
		if (v == null)
			return defaultValue;
		return Boolean.parseBoolean(v);
	}

	protected int $i(String name) {
		return $i(name, 0);
	}

	protected int $i(String name, int defaultValue) {
		String v = $(name);
		if (v == null)
			return defaultValue;
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return 0;
		}
	}

	protected long $l(String name) {
		String v = $(name);
		return Long.parseLong(v);
	}

	protected boolean $has(String name) {
		return $(name) != null;
	}

	protected float $f(String name) {
		String v = $(name);
		try {
			return Float.parseFloat(v);
		} catch (Exception e) {
			return 0;
		}
	}

	protected String[] $$(String name) {
		return request.getParameterValues(name);
	}

	protected int[] $$i(String name) {
		String[] ss = $$(name);
		int[] ns = new int[ss.length];
		for (int i = 0; i < ss.length; i++)
			ns[i] = Integer.parseInt(ss[i]);
		return ns;
	}

	protected float[] $$f(String name) {
		String[] ss = $$(name);
		float[] ns = new float[ss.length];
		for (int i = 0; i < ss.length; i++)
			ns[i] = Float.parseFloat(ss[i]);
		return ns;
	}

	protected File $File() throws AppException {
		return request.getFile();
	}

	protected TempUpFile $TFile() throws AppException {
		return request.getTempUpFile();
	}

	protected File[] $$File() throws AppException {
		return request.getFiles();
	}

	protected List<TempUpFile> $$TFile() {
		return request.getTempUpFiles();
	}

	/**
	 * 察看上传进度，一般用?_uploadid=XXXXXX
	 * 
	 * @param ProcListener
	 */
	public ProcListener getUploadProcess() throws AppException, Exception {
		return request.getUploadProcess();
	}

	protected void checkAuthImageCode(String paramName) throws AppException {
		String submitAuthImageCode = request.getParameter(paramName);
		String realAuthImageCode = (String) getSession(paramName);
		delSession(paramName);
		if (submitAuthImageCode != null
				&& submitAuthImageCode.equalsIgnoreCase(realAuthImageCode))
			return;
		throw new AppException("验证码错误");
	}

	protected void clearActorId() {
		delSessionVar(actorSessionName);
		session.invalidate();
	}

	protected void actorLogout() throws AppException {
		clearActorId();
		afterActorLogout();
	}

	protected abstract void afterActorLogout() throws AppException;

	public abstract void dealWithNoSession();

	protected abstract String getTheme() throws AppException;

	public File createTempFile() throws IOException {
		return request.createTempFile();
	}
	
	public File createTempFile(boolean autoDel) throws IOException {
		return request.createTempFile(autoDel);
	}
	
	
	public void __mailInnerImg() {
		String path = $("path");
		String uid = $("uid");
		String innerattpath = path + uid;
		SkyImage skyImage = new SkyImageImpl(innerattpath);
		r.setImage(skyImage.getImage());
	}
}
