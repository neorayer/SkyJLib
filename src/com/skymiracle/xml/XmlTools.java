package com.skymiracle.xml;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;

public class XmlTools {

	public static Element getElement(Dao dao) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		return getElement(dao.getClass().getSimpleName(), dao);

	}

	public static String getXml(String elName, Object[] objs)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		return getElement(elName, objs).asXML();
	}

	public static String getXml(String elName, Collection objs)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		return getElement(elName, objs).asXML();
	}

	public static Object[] getObjects(String elName, String xml)
			throws DocumentException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Document document = DocumentHelper.parseText(xml);
		Element el = (Element) document.selectSingleNode(elName);
		List<Element> elList = el.elements();
		Object[] objects = new Object[elList.size()];
		int i = -1;
		for (Element subEl : elList) {
			i++;
			String className = subEl.attributeValue("Class");
			Class type = Class.forName(className);
			String text = subEl.getTextTrim();
			if (Dao.class.isAssignableFrom(type)) {
				Object obj = getDao(type, subEl);
				objects[i] = obj;
			} else if (type == Boolean.class)
				objects[i] = Boolean.parseBoolean(text);
			else if (type == Short.class)
				objects[i] = Short.parseShort(text);
			else if (type == Byte.class)
				objects[i] = Byte.parseByte(text);
			else if (type == Integer.class)
				objects[i] = Integer.parseInt(text);
			else if (type == Long.class)
				objects[i] = Long.parseLong(text);
			else if (type == Float.class)
				objects[i] = Float.parseFloat(text);
			else if (type == Double.class)
				objects[i] = Double.parseDouble(text);
			else
				objects[i] = text;
		}
		return objects;
	}

	public static Element getElement(String elName, Object[] objs)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Element el = new DefaultElement(elName);
		for (Object obj : objs)
			el.add(getElement(obj));
		return el;
	}

	public static Element getElement(Object obj)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		return getElement(obj.getClass().getSimpleName(), obj);
	}

	public static String getXml(Dao dao) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Element el = getElement(dao);
		return el.asXML();
	}

	public static String getXml(Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Element el = getElement(obj);
		return el.asXML();
	}

	public static Element getElement(String elName, Dao dao)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(dao.getClass());
		Element el = new DefaultElement(elName);
		el.addAttribute("Class", dao.getClass().getName());
		for (int i = 0; i < attrs.length; i++) {
			BeanAttr attr = attrs[i];
			Object value = attr.getMethod.invoke(dao, new Object[] {});
			if (value == null)
				continue;
			if (value instanceof Dao[])
				el.add(getElement(attr.fieldname, (Dao[]) value));
			else if (value instanceof Dao)
				el.add(getElement(attr.fieldname, (Dao) value));
			else {
				String strValue = value.toString();
				if (attr.type == String[].class)
					strValue = Dao.stringArrayToString((String[]) value);
				Element subEl = el.addElement(attr.fieldname).addText(strValue);
				subEl.addAttribute("Class", value.getClass().getName());
			}
		}
		return el;
	}

	public static Element getElement(String elName, Object obj)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(obj.getClass());
		Element el = new DefaultElement(elName);
		el.addAttribute("Class", obj.getClass().getName());
		for (int i = 0; i < attrs.length; i++) {
			BeanAttr attr = attrs[i];
			Object value = attr.getMethod.invoke(obj, new Object[] {});
			if (value == null)
				continue;
			if (value instanceof Dao[])
				el.add(getElement(attr.fieldname, (Dao[]) value));
			else if (value instanceof Dao)
				el.add(getElement(attr.fieldname, (Dao) value));
			else {
				String strValue = value.toString();
				if (attr.type == String[].class)
					strValue = Dao.stringArrayToString((String[]) value);
				Element subEl = el.addElement(attr.fieldname).addText(strValue);
				subEl.addAttribute("Class", value.getClass().getName());
			}
		}
		return el;
	}

	public static Element getElement(String elName, Dao[] daos)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Element el = new DefaultElement(elName);
		for (int i = 0; i < daos.length; i++)
			el.add(getElement(daos[i]));
		return el;
	}

	public static Element getElement(String elName, Collection daos)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Element el = new DefaultElement(elName);
		for (Object obj : daos)
			el.add(getElement(obj));
		return el;
	}

	public static Dao getDao(Class daoClass, String xml)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException, DocumentException {
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			throw e;
		}
		Element el = (Element) document.selectSingleNode(daoClass
				.getSimpleName());
		return getDao(daoClass, el);
	}

	public static Object getObject(Class clazz, String xml)
			throws DocumentException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, ClassNotFoundException {
		Document document = DocumentHelper.parseText(xml);
		Element el = (Element) document.selectSingleNode(clazz.getSimpleName());
		return getObject(clazz, el);
	}

	public static Dao getDao(Class daoClass, Element el)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException {
		// Dao dao = (Dao) daoClass.newInstance();
		Dao dao = (Dao) Class.forName(el.attributeValue("Class")).newInstance();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(dao.getClass());
		for (int i = 0; i < attrs.length; i++) {
			String fieldname = attrs[i].fieldname;
			Element subEl = el.element(fieldname);
			// Class type = attrs[i].type;
			if (subEl == null)
				continue;
			String className = subEl.attributeValue("Class");
			Class type = className == null ? attrs[i].type : Class
					.forName(className);
			String text = subEl == null ? null : subEl.getText();
			if (text == null)
				continue;

			Object arg = null;
			if (type == String[].class)
				arg = text.split("::::");
			else if (type == String.class)
				arg = text;
			else if (type == StringBuffer.class)
				arg = new StringBuffer(text);
			else if (type == long.class)
				arg = new Long(text);
			else if (type == int.class)
				arg = new Integer(text);
			else if (type == short.class)
				arg = new Short(text);
			else if (type == byte.class)
				arg = new Byte(text);
			else if (type == double.class)
				arg = new Double(text);
			else if (type == float.class)
				arg = new Float(text);
			else if (type == boolean.class)
				arg = new Boolean(text);
			else if (Dao.class.isAssignableFrom(type))
				arg = XmlTools.getDao(type, subEl);
			else if (type == Long.class)
				arg = new Long(text);
			else if (type == Integer.class)
				arg = new Integer(text);
			else if (type == Short.class)
				arg = new Short(text);
			else if (type == Byte.class)
				arg = new Byte(text);
			else if (type == Double.class)
				arg = new Double(text);
			else if (type == Float.class)
				arg = new Float(text);
			else if (type == Boolean.class)
				arg = new Boolean(text);
			else {
				arg = getDaos(type, subEl);
			}
			attrs[i].setMethod.invoke(dao, new Object[] { arg });
		}

		return dao;
	}

	public static Object getObject(Class clazz, Element el)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException {
		Object obj = clazz.newInstance();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(clazz);
		for (int i = 0; i < attrs.length; i++) {
			String fieldname = attrs[i].fieldname;
			Element subEl = el.element(fieldname);
			if (subEl == null)
				continue;
			Class type = clazz;
			String text = subEl == null ? null : subEl.getText();
			if (text == null)
				continue;

			Object arg = null;
			if (type == String[].class)
				arg = text.split("::::");
			else if (type == String.class)
				arg = text;
			else if (type == StringBuffer.class)
				arg = text;
			else if (type == long.class)
				arg = new Long(text);
			else if (type == int.class)
				arg = new Integer(text);
			else if (type == short.class)
				arg = new Short(text);
			else if (type == byte.class)
				arg = new Byte(text);
			else if (type == double.class)
				arg = new Double(text);
			else if (type == float.class)
				arg = new Float(text);
			else if (type == boolean.class)
				arg = new Boolean(text);
			else if (Dao.class.isAssignableFrom(type))
				arg = XmlTools.getDao(type, subEl);
			else if (type == Long.class)
				arg = new Long(text);
			else if (type == Integer.class)
				arg = new Integer(text);
			else if (type == Short.class)
				arg = new Short(text);
			else if (type == Byte.class)
				arg = new Byte(text);
			else if (type == Double.class)
				arg = new Double(text);
			else if (type == Float.class)
				arg = new Float(text);
			else if (type == Boolean.class)
				arg = new Boolean(text);
			else {
				arg = getObject(type, subEl);
			}
			Logger.detail(type + " " + arg + " " + arg.getClass());
			attrs[i].setMethod.invoke(obj, new Object[] { arg });
		}

		return obj;
	}

	public static List getDaos(Class daoClass, Element el)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException {
		Class componentType = daoClass.getComponentType();
		List subElList = el.elements(componentType.getSimpleName());
		List daoList = new ArrayList();
		for (int i = 0; i < subElList.size(); i++) {
			daoList.add(getDao(componentType, (Element) subElList.get(i)));
		}
		return daoList;
	}
}
