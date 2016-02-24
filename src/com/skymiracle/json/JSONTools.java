package com.skymiracle.json;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.reflect.ReflectTools;
import com.skymiracle.tree.TreeNode;

public class JSONTools {

	public static JSONObject getJSONObject(Object obj)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		Class objClass = obj.getClass();
		if (ReflectTools.isSimpleClass(objClass)) {
			jo.put(objClass.getSimpleName(), obj.toString());
			return jo;
		}
		BeanAttr[] attrs = BeanAttrPool.getAttrs(obj.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value = attrs[i].getMethod.invoke(obj, new Object[0]);
			if (value == null) {
				if (attrs[i].type == String[].class)
					jo.put(attrs[i].fieldname, "");
				else if (attrs[i].type == String.class)
					jo.put(attrs[i].fieldname, "");
				else if (attrs[i].type == Boolean.class)
					jo.put(attrs[i].fieldname, "");
				else if (attrs[i].type == Collection.class)
					jo.put(attrs[i].fieldname, new JSONArray());
				else
					jo.put(attrs[i].fieldname, "0");
			}else {
				if (attrs[i].type == String[].class){
					String[] values = (String[]) value;
					if(values.length==1&&values[0].equals(""))
						jo.put(attrs[i].fieldname, new JSONArray());
					else
						jo.put(attrs[i].fieldname, getJSONArray((String[]) value));
					}
				else if (value instanceof Dao)
					jo.put(attrs[i].fieldname, getJSONObject(value));
				else if (value instanceof Mdo)
					jo.put(attrs[i].fieldname, getJSONObject(value));
				else if (value instanceof Dao[])
					jo.put(attrs[i].fieldname, getJSONArray((Dao[]) value));
				else if (value instanceof Collection) {
					jo.put(attrs[i].fieldname,
									getJSONArray((Collection) value));
				} else
					jo.put(attrs[i].fieldname, value.toString());
			}
		}
		return jo;
	}

	public static JSONObject getJSONObject(Map map)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		Set keySet = map.keySet();
		Iterator iter = keySet.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = map.get(key);
			if (value == null) {
				
			}else if (value instanceof Dao)
				jo.put(key, getJSONObject(value));
			else if (value instanceof Mdo)
				jo.put(key, getJSONObject(value));
			else if (value instanceof JSONObject)
				jo.put(key, value);
			else if (value instanceof Dao[])
				jo.put(key, getJSONArray((Dao[]) value));
			else if (value instanceof Collection)
				jo.put(key, getJSONArray((Collection) value));
			else
				jo.put(key, value.toString());
		}
		return jo;
	}

	public static String getJSONString(Dao obj) {
		try {
			return getJSONObject(obj).toString();
		} catch (Exception e) {
			Logger.error("JSONTools.getJSONString(obj)", e);
		}
		return "{}";
	}
	
	public static String getJSONString(Mdo<?> obj) {
		try {
			return getJSONObject(obj).toString();
		} catch (Exception e) {
			Logger.error("JSONTools.getJSONString(obj)", e);
		}
		return "{}";
	}

	public static JSONObject getResJSONObject(String flag, Dao obj)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", getJSONObject(obj));
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, Dao obj, String desc)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", getJSONObject(obj));
		jo.put("desc", desc);
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, Object obj)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", getJSONObject(obj));
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, JSONArray ja)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", ja);
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, JSONObject jo)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONObject resJo = new JSONObject();
		resJo.put("res", flag);
		resJo.put("data", jo);
		return resJo;
	}

	private static Object getResJSONObject(String flag, Map map)
			throws JSONException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", getJSONObject(map));
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, String msg)
			throws JSONException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", msg);
		return jo;
	}

	public static JSONObject getResJSONObject(String flag, Collection coll)
			throws JSONException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		return getResJSONObject(flag, coll, "");
	}

	public static JSONObject getResJSONObject(String flag, Collection coll,
			String desc) throws JSONException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		JSONObject jo = new JSONObject();
		jo.put("res", flag);
		jo.put("data", getJSONArray(coll));
		jo.put("desc", desc);
		return jo;
	}

	public static String getResJSONString(String flag, Dao obj) {
		try {
			return getResJSONObject(flag, obj).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (JSONException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (IllegalAccessException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (InvocationTargetException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, JSONArray ja) {
		try {
			return getResJSONObject(flag, ja).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (JSONException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (IllegalAccessException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (InvocationTargetException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		}
		return "{res: 0, data: \"[]\"}";
	}

	public static String getResJSONString(String flag, Object obj) {
		try {
			return getResJSONObject(flag, obj).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (JSONException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (IllegalAccessException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		} catch (InvocationTargetException e) {
			Logger.error("JSONTools.getResJSONString(flag, obj)", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, Dao[] daos) {
		try {
			return JSONTools.getResJSONString(flag, getJSONArray(daos));
		} catch (Exception e) {
			Logger.error("JSONTools.getResJSONString(flag, daos)", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, Map map) {
		try {
			return getResJSONObject(flag, map).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("", e);
		} catch (JSONException e) {
			Logger.error("", e);
		} catch (IllegalAccessException e) {
			Logger.error("", e);
		} catch (InvocationTargetException e) {
			Logger.error("", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, String msg) {
		try {
			return getResJSONObject(flag, msg).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("JSONTools.getResJSONString(flag, String)", e);
		} catch (JSONException e) {
			Logger.error("JSONTools.getResJSONString(flag, String)", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, Collection coll) {
		try {
			return getResJSONObject(flag, coll).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("", e);
		} catch (JSONException e) {
			Logger.error("", e);
		} catch (IllegalAccessException e) {
			Logger.error("", e);
		} catch (InvocationTargetException e) {
			Logger.error("", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}


	public static String getResJSONString(String flag, Dao dao, String desc) {
		try {
			return getResJSONObject(flag, dao, desc).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("", e);
		} catch (JSONException e) {
			Logger.error("", e);
		} catch (IllegalAccessException e) {
			Logger.error("", e);
		} catch (InvocationTargetException e) {
			Logger.error("", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static String getResJSONString(String flag, Collection coll,
			String desc) {
		try {
			return getResJSONObject(flag, coll, desc).toString();
		} catch (IllegalArgumentException e) {
			Logger.error("", e);
		} catch (JSONException e) {
			Logger.error("", e);
		} catch (IllegalAccessException e) {
			Logger.error("", e);
		} catch (InvocationTargetException e) {
			Logger.error("", e);
		}
		return "{res: 0, data: \"json exception\"}";
	}

	public static JSONArray getJSONArray(Dao[] objs)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONArray ja = new JSONArray();
		for (int i = 0; i < objs.length; i++) {
			ja.put(getJSONObject(objs[i]));
		}
		return ja;
	}

	public static JSONArray getJSONArray(Collection coll)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONArray ja = new JSONArray();
		for (Object obj : coll) {
			ja.put(getJSONObject(obj));
		}
		return ja;
	}

	public static JSONArray getJSONArray(Object[] objs)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONArray ja = new JSONArray();
		for (Object obj : objs) {
			ja.put(getJSONObject(obj));
		}
		return ja;
	}

	public static String getJSONString(Collection coll)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		return getJSONArray(coll).toString();
	}

	public static String getJSONString(String[] strs)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		return getJSONArray(strs).toString();
	}

	public static JSONArray getJSONArray(String[] strings)
			throws IllegalArgumentException, JSONException,
			IllegalAccessException, InvocationTargetException {
		JSONArray ja = new JSONArray();
		for (int i = 0; i < strings.length; i++) {
			ja.put(strings[i]);
		}
		return ja;
	}

	public static String getJSONString(Dao[] objs) {
		try {
			return getJSONArray(objs).toString();
		} catch (Exception e) {
			Logger.error("JSONTools.getJSONString(objs)", e);
		}
		return "{}";
	}

	public static String getResJSONString(String flag, TreeNode treeNode) {
		try {
			return getResJSONString(flag, treeNode.getJSONObject());
		} catch (Exception e) {
			Logger.error("", e);
			return "{res: 0, data: \"json exception\"}";
		}
	}

	public static String getResJSONString(String flag, JSONObject jo) {
		try {
			return getResJSONObject(flag, jo).toString();
		} catch (Exception e) {
			Logger.error("", e);
			return "{res: 0, data: \"json exception\"}";
		}
	}
}
