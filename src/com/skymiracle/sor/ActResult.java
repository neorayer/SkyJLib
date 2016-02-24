package com.skymiracle.sor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skymiracle.image.SkyImage;
import com.skymiracle.image.SkyImageImpl;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.PagedList;
import com.skymiracle.reflect.ReflectTools;
import com.skymiracle.sor.exception.AppException;

public class ActResult {
	protected static ThreadLocal<ActResult> resultThreadLocal = new ThreadLocal<ActResult>();

	public static ActResult get() {
		ActResult vs = resultThreadLocal.get();
		if (vs == null) {
			vs = new ActResult();
			resultThreadLocal.set(vs);
		}
		return vs;
	}

	public static void clear() {
		resultThreadLocal.set(null);
	}

	public class PageResult {
		public int pageCount;
		public int pageNum;
		public int countPerPage;
	}

	private String desc = "";

	private PageResult pageResult = new PageResult();

	private List<Object> dataColl = null;

	private Dao dao;

	private Mdo mdo;

	private AppException appEx = new AppException("appException");

	private Map<String, Object> dataMap = new HashMap<String, Object>();

	private String redirectTo;

	private String fileName;

	private File file;

	private String imageFormat = "JPG";

	private Image authImage;

	private Image image;

	private Object actor;

	private String portalLet;

	private JSONObject json;

	private String xmlText;

	private JSONArray ja;

	private Class<?> homeAPI;

	private Object homeImpl;

	private String js;

	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<Object> getDataColl() {
		return dataColl;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public Dao getDao() {
		return dao;
	}

	public Mdo getMdo() {
		return mdo;
	}

	public void setMdo(Mdo mdo) {
		this.mdo = mdo;
	}

	/**
	 * 一般用于Dao List
	 * 
	 * @param data
	 */
	public void putColl(Collection<?> dataColl) {
		if (dataColl == null)
			return;
		if (this.dataColl == null)
			this.dataColl = new LinkedList<Object>();
		if (ReflectTools.isHeritate(dataColl.getClass(), Collection.class))
			this.dataColl.addAll(dataColl);
	}

	public void putColl(Collection<?> dataColl, int pageCount, int pageNum,
			int ocountPerPageuntPerPage) {
		if (dataColl == null)
			return;
		if (this.dataColl == null)
			this.dataColl = new LinkedList<Object>();
		if (ReflectTools.isHeritate(dataColl.getClass(), Collection.class))
			this.dataColl.addAll(dataColl);
		pageResult.pageCount = pageCount;
		pageResult.pageNum = pageNum;
		pageResult.countPerPage = ocountPerPageuntPerPage;
		this.desc = "" + pageResult.pageCount;
	}

	public void putColl(PagedList<?> pagedList) {
		if (pagedList == null)
			return;
		if (this.dataColl == null)
			this.dataColl = new LinkedList<Object>();
		this.dataColl.addAll(pagedList);
		pageResult.countPerPage = pagedList.getCountPerPage();
		this.desc = "" + pagedList.getAllCount();
	}

	/**
	 * 一般用于单个Dao
	 * 
	 * @param data
	 */
	public void putDao(Dao dao) {
		if (dao == null)
			return;
		this.dao = dao;
	}

	/**
	 * 一般用于Jsp
	 * 
	 * @param data
	 */
	public void putMap(String key, Object value) {
		this.dataMap.put(key, value);
	}

	public void putError(String error) {
		appEx.addError(error);
	}

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	/**
	 * 一般用down格式
	 * 
	 * @param inputStream
	 */
	public void putFile(File file, String fileName) {
		this.file = file;
		this.fileName = fileName;
	}

	public String getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	public Image getAuthImage() {
		return authImage;
	}

	public void setAuthImage(String code) {
		SkyImage si = new SkyImageImpl(SkyImage.FORMAT_JPG);
		si.createAuthImage(code, new Font("Arial", Font.BOLD, 18), Color.WHITE,
				Color.BLACK, 0.1);
		this.authImage = si.getImage();
	}

	public void setActor(Object actor) {
		this.actor = actor;
	}

	public Object getActor() {
		return actor;
	}

	public String getPortalLet() {
		return portalLet;
	}

	public void setPortalLet(String portalLet) {
		this.portalLet = portalLet;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setImage(Image image, String imageFormat) {
		this.image = image;
		this.imageFormat = imageFormat;
	}

	public String getActorClassName() {
		if (this.actor == null)
			return null;
		return this.actor.getClass().getSimpleName();
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JSONArray getJa() {
		return ja;
	}

	public void setJa(JSONArray ja) {
		this.ja = ja;
	}

	public void setJson(JSONArray ja) {
		this.ja = ja;
	}

	public Class<?> getHomeAPI() {
		return homeAPI;
	}

	public void setHomeAPI(Class<?> homeAPI) {
		this.homeAPI = homeAPI;
	}

	public Object getHomeImpl() {
		return homeImpl;
	}

	public void setHomeImpl(Object homeImpl) {
		this.homeImpl = homeImpl;
	}

	// 主要用于Hessian
	public void setHessianParam(Object homeImpl, Class<?> homeAPI) {
		this.homeImpl = homeImpl;
		this.homeAPI = homeAPI;
	}

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

}
