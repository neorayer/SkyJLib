package com.skymiracle.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.tree.TreeNode;

public class DNAJson {
	public static String FLAG_TRUE = "yes";

	public static String FLAG_FALSE = "no";

	public static String getResJSONString(List list) {
		return JSONTools.getResJSONString(FLAG_TRUE, list);
	}

	public static String getResJSONString(Collection coll, String desc) {
		return JSONTools.getResJSONString(FLAG_TRUE, coll, desc);
	}

	public static String getResJSONString(List list, int desc) {
		return JSONTools.getResJSONString(FLAG_TRUE, list, desc + "");
	}

	public static String getResJSONString(Dao dao, String desc) {
		return JSONTools.getResJSONString(FLAG_TRUE, dao, desc);
	}
	

	public static String getResJSONString(TreeNode treeNode) {
		return JSONTools.getResJSONString(FLAG_TRUE, treeNode);
	}

	public static String getResTrueJSONString() {
		return JSONTools.getResJSONString(FLAG_TRUE, "");
	}

	public static String getResFalseJSONString(String msg) {
		return JSONTools.getResJSONString(FLAG_FALSE, msg);
	}

	public static String getResJSONString(Exception e) {
		return getResJSONString(e, true);
	}

	public static String getResJSONString(Exception e, boolean isNeedLog) {
		if (isNeedLog)
			Logger.debug("", e);
		return JSONTools.getResJSONString(FLAG_FALSE, e.getMessage());
	}

	public static String getResJSONString(Exception e,
			Map<String, String> dictMap) {
		Logger.debug("", e);
		String msg = dictMap.get(e.getMessage());
		msg = msg == null ? e.getMessage() : msg;
		return JSONTools.getResJSONString(FLAG_FALSE, msg);
	}

	public static String getResJSONString(Dao dao) {
		return JSONTools.getResJSONString(FLAG_TRUE, dao);
	}

	public static String getResJSONString(Object obj) {
		return JSONTools.getResJSONString(FLAG_TRUE, obj);
	}


	public static String getResJSONString(Map map, String desc) {
		return JSONTools.getResJSONString(FLAG_TRUE, map);
	}


	public static String getResJSONString(JSONObject jo) {
		return JSONTools.getResJSONString(FLAG_TRUE, jo);
	}

	public static String getResJSONString(Dao[] daos) {
		return JSONTools.getResJSONString(FLAG_TRUE, daos);
	}

	public static String getResJSONString(Map map) {
		return JSONTools.getResJSONString(FLAG_TRUE, map);
	}

	public static String getResJSONString(JSONArray ja) {
		return JSONTools.getResJSONString(FLAG_TRUE, ja);
	}

	public static JSONObject getResJSONObject(JSONArray ja) throws Exception {
		return JSONTools.getResJSONObject(FLAG_TRUE, ja);
	}

	public static JSONObject getResJSONObject(List list) throws Exception {
		return JSONTools.getResJSONObject(FLAG_TRUE, list);
	}

}
