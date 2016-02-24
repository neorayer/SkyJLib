package com.skymiracle.mdo5;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.skymiracle.mdo5.Mdo.Auto;
import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.util.CalendarUtil;

/**
 * 一个继承自HashMap的属性集类。专门用于配合Dao使用。在查询时经常用到。
 * 
 * @author skymiracle
 * @see com.skymiracle.mdo4.Dao
 */
public class MdoMap extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1398760130303670271L;
	
	public StringBuffer orderBy = new StringBuffer();
	
	public int limitBegin = 0;

	public int limitCount = -1;

	public  MdoMap filledBy(Class<? extends Mdo<?>> mdoClass, HttpServletRequest request, String srcCharset,
			String destCharset) throws UnsupportedEncodingException {
		MdoField[] mdoFields = MdoReflector.getMdoFields(mdoClass);
		for (MdoField mf : mdoFields) {
			String s = request.getParameter(mf.name);
			if (s == null)
				continue;
			Object arg = Mdo.convertObject(mf.type, s, srcCharset, destCharset);
			put(mf.name, arg);
		}
		return this;
	}

	public  MdoMap filledBy(Class<? extends Mdo<?>>mdoClass, Map<String,String> map) throws UnsupportedEncodingException {
		MdoField[] mdoFields = MdoReflector.getMdoFields(mdoClass);
		for (MdoField mf : mdoFields) {
			String s = map.get(mf.name);
			if (s == null)
				continue;
			Object arg = Mdo.convertObject(mf.type, s, "UTF-8", "UTF-8");
			put(mf.name, arg);
		}
		return this;
	}

	public MdoMap filledBy(String fieldsFormat, Object... values) {
		String[] ff = fieldsFormat.split(":");
		
		String[] ss = ff[0].split(",");
		for (int i = 0; i < ss.length; i++) {
			ss[i] = ss[i].trim().toLowerCase();
			ss[i] = parseOrderBy(ss[i]);
			if (values == null || values.length <= i)
				continue;
			put(ss[i], values[i]);
		}
		
		if(!"".equals(orderBy.toString()))
			orderBy.deleteCharAt(orderBy.length() - 1);
		
		// limit	
		if(ff.length > 1) {
			parseLimit(ff[1]);
		}
		
		return this;
	}

	public void put(Object key, boolean v) {
		put(key, new Boolean(v));
	}

	public void put(Object key, byte v) {
		put(key, new Byte(v));
	}

	public void put(Object key, double v) {
		put(key, new Double(v));
	}

	public void put(Object key, float v) {
		put(key, new Float(v));
	}

	public void put(Object key, int v) {
		put(key, new Integer(v));
	}

	public void put(Object key, long v) {
		put(key, new Long(v));
	}

	public void put(Object key, short v) {
		put(key, new Short(v));
	}

	public void put(String key, boolean v) {
		put(key.toLowerCase(), new Boolean(v));
	}

	public void put(String key, byte v) {
		put(key.toLowerCase(), new Byte(v));
	}

	public void put(String key, double v) {
		put(key.toLowerCase(), new Double(v));
	}

	public void put(String key, float v) {
		put(key.toLowerCase(), new Float(v));
	}

	public void put(String key, int v) {
		put(key.toLowerCase(), new Integer(v));
	}

	public void put(String key, long v) {
		put(key.toLowerCase(), new Long(v));
	}

	public void put(String key, short v) {
		put(key.toLowerCase(), new Short(v));
	}

	public void put(String key, String v) {
		super.put(key.toLowerCase(), v);
	}
	public String toConditionSQL() {
		return RdbmsSession.getSQL(this).toString();
	}
	
	private String parseOrderBy(String str) {
		if(str.endsWith("+")) {
			str = str.replace("+", "");
			orderBy.append(str + " ASC,");
		}
		else if(str.endsWith("-")) {
			str = str.replace("-", "");
			orderBy.append(str + " DESC,");
		}
		return str;
	}
	
	private void parseLimit(String limitStr) {
		String[] limits = limitStr.split(",");
		if(limits.length > 1) {
			// 10:20, 从 第10条开始，取 20条记录
			limitBegin = Integer.parseInt(limits[0]);
			limitCount = Integer.parseInt(limits[1]);
		}
		else {
			// 10, 从第 0 条开始, 取10条记录
			limitBegin = 0;
			limitCount = Integer.parseInt(limits[0]);
		}
	}

	public void filledByAutoModify(Class<? extends Mdo<?>> mdoClass) {
		MdoField[] mdoFields =MdoReflector.getMdoFields(mdoClass);
		for (MdoField mf : mdoFields) {
			if (mf.autoType == null)
				continue;
			String v = null;
			if (mf.autoType == Auto.Type.ModifyDate)
				v = CalendarUtil.getLocalDate();
			else if (mf.autoType == Auto.Type.ModifyDateTime)
				v = CalendarUtil.getLocalDateTime();
			else
				continue;
			put(mf.name, v);
		}
	}

}
