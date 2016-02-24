package com.skymiracle.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;

public class HttpRequestTools {
	public static DaoAttrSet getDaoAttrSet(Class<?> daoClass,
			HttpServletRequest request) throws UnsupportedEncodingException {
		return getDaoAttrSet(daoClass, request, "UTF-8");
	}

	public static DaoAttrSet getDaoAttrSet(Class<?> daoClass,
			HttpServletRequest request, String charset)
			throws UnsupportedEncodingException {
		return getDaoAttrSet(daoClass, request, null, charset);
	}

	public static DaoAttrSet getDaoAttrSet(Class<?> daoClass,
			HttpServletRequest request, String srcCharset, String destCharset)
			throws UnsupportedEncodingException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
		DaoAttrSet attrSet = new DaoAttrSet();

		for (BeanAttr attr : attrs) {
			String s = request.getParameter(attr.fieldname);
			if (s == null)
				continue;
			Object arg = convertObject(attr.type, s, srcCharset, destCharset);
			attrSet.put(attr.fieldname, arg);
		}

		return attrSet;
	}

	
	public static DaoAttrSet getDaoAttrSetFromISO(Class<?> daoClass,
			HttpServletRequest request, String charset)
			throws UnsupportedEncodingException {
		return getDaoAttrSet(daoClass, request, "ISO-8859-1", charset);
	}

	public static <T> T getDao(Class<T> daoClass, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		return getDao(daoClass, request, "UTF-8");
	}

	public static <T> T getDao(Class<T> daoClass, HttpServletRequest request,
			String charset) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, UnsupportedEncodingException {
		return getBean(daoClass, request, charset);
	}

	public static <T> T getDao(Class<T> daoClass, HttpServletRequest request,
			String srcCharset, String destCharset)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		return getBean(daoClass, request, srcCharset, destCharset);
	}

	public static <T> T getBean(Class<T> beanClass, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		return getBean(beanClass, request, "UTF-8");
	}

	public static <T> List<T> getBeans(Class<T> beanClass,
			HttpServletRequest request, String... fields)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		return getBeansFromDefault(beanClass, request, "UTF-8", fields);
	}

	public static <T> T getBean(Class<T> beanClass, HttpServletRequest request,
			String charset) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, UnsupportedEncodingException {
		return getBean(beanClass, request, null, charset);
	}

	public static <T> List<T> getBeansFromDefault(Class<T> beanClass,
			HttpServletRequest request, String charset, String... fields)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		return getBeans(beanClass, request, null, charset, fields);
	}

	public static <T> T getBean(Class<T> beanClass, HttpServletRequest request,
			String srcCharset, String destCharset)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(beanClass);
		T obj = beanClass.newInstance();
		for (BeanAttr attr : attrs) {
			String s = request.getParameter(attr.fieldname);
			if (s == null)
				continue;
			Object arg = convertObject(attr.type, s, srcCharset, destCharset);
			attr.setMethod.invoke(obj, new Object[] { arg });
		}

		return obj;
	}

	public static <T> List<T> getBeans(Class<T> beanClass,
			HttpServletRequest request, String srcCharset,
			String destCharset,  String... fields) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, UnsupportedEncodingException {
		//BeanAttr[] attrs = BeanAttrPool.getAttrs(beanClass);

		String[] values = request.getParameterValues(fields[0].toLowerCase());
		List<T> beans = new LinkedList<T>();
		for (int i=0; i<values.length; i++) {
			T obj = beanClass.newInstance();
			for(String field: fields) {
				String s = request.getParameterValues(field.toLowerCase())[i];

				BeanAttr attr = BeanAttrPool.getAttr(beanClass, field);
				Object arg = convertObject(attr.type, s, srcCharset,
						destCharset);
				attr.setMethod.invoke(obj, new Object[] { arg });
			}
			beans.add(obj);
		}
		return beans;
	}

	@SuppressWarnings("unchecked")
	private static Object convertObject(Class<?> type, String s,
			String srcCharset, String destCharset)
			throws UnsupportedEncodingException {
		Object arg = null;
		if (type == String.class) {
			if (srcCharset == null)
				arg = new String(s.getBytes(), destCharset);
			else
				arg = new String(s.getBytes(srcCharset), destCharset);
		} else if (type == StringBuffer.class) {
			if (srcCharset == null)
				arg = new StringBuffer(new String(s.getBytes(), destCharset));
			else
				arg = new StringBuffer(new String(s.getBytes(srcCharset),
						destCharset));
		} else if (type == long.class)
			arg = new Long(Long.parseLong(s));
		else if (type == int.class)
			arg = new Integer(Integer.parseInt(s));
		else if (type == short.class)
			arg = new Short(Short.parseShort(s));
		else if (type == byte.class)
			arg = new Byte(Byte.parseByte(s));
		else if (type == double.class)
			arg = new Double(Double.parseDouble(s));
		else if (type == float.class)
			arg = new Float(Float.parseFloat(s));
		else if (type == boolean.class)
			arg = new Boolean(Boolean.parseBoolean(s));
		else if (type.isEnum()) {
			arg = Enum.valueOf((Class<Enum>) type, s);
		}

		return arg;
	}


	public static boolean isMultiPartForm(HttpServletRequest request) {
		String contentType = request.getContentType();
		if (contentType == null)
			return false;
		contentType = contentType.toLowerCase();
		if (contentType.indexOf("multipart/form-data") < 0)
			return false;
		return true;
	}
	
	public static String getServletPath(HttpServletRequest request) {
		String servletPath = (String) request
				.getAttribute("javax.servlet.include.servlet_path");
		if (servletPath == null)
			servletPath = request.getServletPath();

		String requestUri = request.getRequestURI();
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

		int startIndex = request.getContextPath().equals("") ? 0 : request
				.getContextPath().length();
		int endIndex = request.getPathInfo() == null ? requestUri.length()
				: requestUri.lastIndexOf(request.getPathInfo());

		if (startIndex > endIndex) { // this should not happen
			endIndex = startIndex;
		}

		return requestUri.substring(startIndex, endIndex);
	}
	
}
