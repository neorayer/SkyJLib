package com.skymiracle.mdo4;

import java.util.HashMap;

import com.skymiracle.sql.SQLSession;

/**
 * 一个继承自HashMap的属性集类。专门用于配合Dao使用。在查询时经常用到。
 * 
 * @author skymiracle
 * @see com.skymiracle.mdo4.Dao
 */
public class DaoAttrSet extends HashMap<String,Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1398760130303670271L;

	public void put(Object key, long v) {
		put(key, new Long(v));
	}

	public void put(Object key, int v) {
		put(key, new Integer(v));
	}

	public void put(Object key, short v) {
		put(key, new Short(v));
	}

	public void put(Object key, byte v) {
		put(key, new Byte(v));
	}

	public void put(Object key, boolean v) {
		put(key, new Boolean(v));
	}

	public void put(Object key, double v) {
		put(key, new Double(v));
	}

	public void put(Object key, float v) {
		put(key, new Float(v));
	}

	public void put(String key, long v) {
		put(key.toLowerCase(), new Long(v));
	}

	public void put(String key, int v) {
		put(key.toLowerCase(), new Integer(v));
	}

	public void put(String key, short v) {
		put(key.toLowerCase(), new Short(v));
	}

	public void put(String key, byte v) {
		put(key.toLowerCase(), new Byte(v));
	}

	public void put(String key, boolean v) {
		put(key.toLowerCase(), new Boolean(v));
	}

	public void put(String key, double v) {
		put(key.toLowerCase(), new Double(v));
	}

	public void put(String key, float v) {
		put(key.toLowerCase(), new Float(v));
	}

	public void put(String key, String v) {
		super.put(key.toLowerCase(), v);
	}

	public String toConditionSQL() {
		return SQLSession.getSQL(this).toString();
	}
}
