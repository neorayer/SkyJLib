package com.skymiracle.sor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.skymiracle.http.HttpRequestTools;
import com.skymiracle.http.HttpUploader;
import com.skymiracle.http.ParamsMap;
import com.skymiracle.http.HttpUploader.ProcListener;
import com.skymiracle.http.HttpUploader.TempUpFile;
import com.skymiracle.http.HttpUploader.UploadResultSet;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo5.MList;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.MdoMap;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.validate.ValidateException;

public class SorRequest extends HttpServletRequestWrapper {

	public static String Project_Real_Path;

	private Map<String, String> pathParams;

	private String domain;

	private String subDomain;

	private String tempDir = "/tmp";

	private ParamsMap multiPartParams = new ParamsMap();

	private List<TempUpFile> mpFiles = new LinkedList<TempUpFile>();

	private List<File> tempFiles = new LinkedList<File>();

	protected String UPLOAD_ID_NAME = "_uploadid";

	protected Object threadLocalActor;

	public SorRequest(HttpServletRequest request) {
		super(request);
	}

	public String getSubDomain() {
		if (subDomain == null) {
			String host = super.getHeader("host");
			String[] ss = host.split("\\.");
			subDomain = ss[0];
		}
		return subDomain;
	}

	public String getDomain() {
		if (domain == null) {
			String host = super.getHeader("host");
			if (host.indexOf(".") > 0)
				domain = host.substring(host.indexOf("."));
			else
				domain = host;
		}
		return domain;
	}

	public String replaceSubDomain(String subDomain) {
		String url = this.getRequestURL().toString();
		return url.replaceAll("http://\\w+\\.", "http://" + subDomain + ".");
	}

	public String get(String name) {
		if (name == null)
			return null;

		if (name.startsWith(":"))
			return pathParams.get(name);

		String v = getParameter(name);
		if (v == null)
			v = multiPartParams.get(name);

		return v;
	}

	public void setPathParams(Map<String, String> pathParams) {
		this.pathParams = pathParams;
	}

	public boolean hasLayout() {
		if (isXmlHttpRequest()) {
			return true;
		}

		try {
			return (Boolean) this
					.getAttribute("com.skymiracle.sor.SorRequest.layout");
		} catch (Exception e) {
			return false;
		}
	}

	public void setPL(String path) {
		this.setAttribute("PL", path);
		this.setAttribute("com.skymiracle.sor.SorRequest.layout", true);
	}

	public boolean isMultiPartForm() {
		String contentType = getContentType();
		if (contentType == null)
			return false;
		contentType = contentType.toLowerCase();
		return contentType.contains("multipart/form-data");
	}

	public <T extends Dao> T getBean(Class<T> clz) throws ValidateException,
			Exception {
		try {
			T dao = HttpRequestTools.getBean(clz, this);
			if (isMultiPartForm())
				dao.fetchFromMap(multiPartParams);
			return dao;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ValidateException)
				throw (ValidateException) cause;
			throw e;
		}
	}

	public <T extends Mdo<?>> T getMdo(Class<T> clz) throws AppException,
			Exception {
		T mdo = Mdo.instance(clz);
		mdo.filledBy(this, null, null);
		if (isMultiPartForm())
			mdo.filledBy(this.multiPartParams);
		return mdo;
	}

	public <T> List<T> getBeans(Class<T> clz, String... field)
			throws ValidateException, Exception {
		return HttpRequestTools.getBeans(clz, this, field);
	}

	public <T extends Mdo<T>> MList<T> getMdos(Class<T> clz, String... field)
			throws ValidateException, Exception {
		MList<T> list = new MList<T>();
		list.addAll(HttpRequestTools.getBeans(clz, this, field));
		return list;
	}

	public DaoAttrSet getDaoAttrSet(Class<? extends Dao> clz)
			throws UnsupportedEncodingException {
		DaoAttrSet das = HttpRequestTools.getDaoAttrSet(clz, this);
		if (isMultiPartForm())
			das.putAll(this.multiPartParams);
		return das;
	}

	public MdoMap getMdoMap(Class<? extends Mdo<?>> clz)
			throws UnsupportedEncodingException {
		MdoMap mdoMap = new MdoMap().filledBy(clz, this, null, null);
		if (isMultiPartForm())
			mdoMap.filledBy(clz, this.multiPartParams);
		return mdoMap;
	}

	/**
	 * 如果要监控上传进度，必须在url后面跟上get方式的参数以表达上传任务ID，
	 * http://xxx//....?$UPLOAD_ID_NAME=XXXXX
	 * 
	 * @param actResult
	 * @throws Exception
	 */
	public void doUpload() throws Exception {
		if (isMultiPartForm()) {

			HttpUploader uploader = new HttpUploader(getCharacterEncoding());
			uploader.setTmpDirPath(tempDir);
			String id = super.getParameter(UPLOAD_ID_NAME);
			UploadResultSet uploadResultSet = uploader.doUpload(this, id);
			if (uploadResultSet == null)
				return;

			this.multiPartParams = uploadResultSet.getParamsMap();
			this.mpFiles = uploadResultSet.getTempUpFiles();
			for (TempUpFile f : mpFiles)
				tempFiles.add(new File(f.getTmpUpPath()));
			fillFromMultiPart();
		} else {
			fillFromRequest();
		}
	}

	private void fillFromMultiPart() {
		for (Map.Entry<String, String> entry : multiPartParams.entrySet())
			this.setAttribute(entry.getKey(), entry.getValue());
	}

	@SuppressWarnings("unchecked")
	private void fillFromRequest() throws UnsupportedEncodingException {
		Map<String, String> reqParamsMap = super.getParameterMap();
		for (Map.Entry<String, String> entry : reqParamsMap.entrySet()) {
			String key = entry.getKey();
			String value = getParameter(key);
			value = new String(value.getBytes("ISO-8859-1"), getCharacterEncoding());
			this.setAttribute(key, value);
		}
	}

	public File createTempFile() throws IOException {
		return createTempFile(true);
	}

	public File createTempFile(boolean autoDel) throws IOException {
		String tempPath = tempDir + "/SOR_" + UUID.randomUUID().toString();
		File file = new File(tempPath);
		if (!file.createNewFile())
			throw new IOException("系统无法创建临时文件");
		if (autoDel)
			tempFiles.add(file);
		return file;
	}

	public void clear() {
		for (File f : tempFiles) {
			f.delete();
		}
	}

	public String[] getParameterValues(String name) {
		String[] vs = super.getParameterValues(name);
		if (vs == null)
			vs = multiPartParams.getValues(name);
		return vs;
	}

	public File getFile() throws AppException {
		if (mpFiles.size() <= 0)
			throw new AppException("你未上传任何文件");
		return new File(mpFiles.get(0).getTmpUpPath());
	}

	public TempUpFile getTempUpFile() throws AppException {
		if (mpFiles.size() <= 0)
			throw new AppException("你未上传任何文件");
		return mpFiles.get(0);
	}

	public File[] getFiles() {
		List<File> files = new LinkedList<File>();
		for (TempUpFile mpFile : mpFiles) {
			files.add(new File(mpFile.getTmpUpPath()));
		}
		return files.toArray(new File[0]);
	}

	public List<TempUpFile> getTempUpFiles() {
		return this.mpFiles;
	}

	/**
	 * 察看上传进度，一般用uploadProcess.json?_uploadid=XXXXXX
	 * 
	 */
	public ProcListener getUploadProcess() {
		String uploadID = this.getParameter(UPLOAD_ID_NAME);
		if (uploadID == null)
			return null;
		return HttpUploader.getListener(uploadID);
	}

	public boolean isGet() {
		return "get".equalsIgnoreCase(super.getMethod());
	}

	public boolean isXmlHttpRequest() {
		return "XMLHttpRequest".equalsIgnoreCase(super
				.getHeader("X-Requested-With"));
	}

	public Object getCurURL() {
		String url = getRequestURL().toString();
		String query = getQueryString();
		return query == null ? url : url + "?" + query;
	}

	public Cookie findCookie(String cookieName) {
		Cookie[] cookies = getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies)
			if (cookieName.equals(cookie.getName()))
				return cookie;

		return null;
	}

	public Cookie createCookie(String key, String value, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(maxAge);
		cookie.setDomain(getDomain());
		cookie.setPath(getContextPath());

		return cookie;
	}

	public String getAppPath() {
		String relativeServletPath = (String) getAttribute("javax.servlet.include.servlet_path");
		if(relativeServletPath != null)
			return relativeServletPath;
		
		String servletPath = super.getServletPath();

		String requestUri = super.getRequestURI();
		// Detecting other characters that the servlet container cut off (like
		// anything after ';')
		if (requestUri != null && servletPath != null
				&& !requestUri.endsWith(servletPath)) {
			int pos = requestUri.indexOf(servletPath);
			if (pos > -1) {
				servletPath = requestUri.substring(requestUri
						.indexOf(servletPath));
			}
		}

		if (null != servletPath && !"".equals(servletPath)) {
			return servletPath;
		}

		int startIndex = super.getContextPath().equals("") ? 0 : super
				.getContextPath().length();
		int endIndex = super.getPathInfo() == null ? requestUri.length()
				: requestUri.lastIndexOf(super.getPathInfo());

		if (startIndex > endIndex) { // this should not happen
			endIndex = startIndex;
		}

		return requestUri.substring(startIndex, endIndex);
	}

	public String getProjectBasePath() {
		return "http://" + super.getHeader("host") + super.getContextPath()
				+ "/";
	}

	public Object getThreadLocalActor() {
		return super.getAttribute("__THREAD__LOCALHOST_ACTOR__");
	}

	public void setThreadLocalActor(Object actor) {
		super.setAttribute("__THREAD__LOCALHOST_ACTOR__", actor);
	}
}
