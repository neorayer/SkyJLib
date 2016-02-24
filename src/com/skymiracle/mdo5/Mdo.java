package com.skymiracle.mdo5;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.util.CalendarUtil;
import com.skymiracle.util.StringUtils;
import com.skymiracle.validate.Validate;

public abstract class Mdo<T extends Mdo<T>> {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Desc {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface EnumNames {
		String[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface IsPwd {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface IsPwd2 {
		String equal();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Required {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Title {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Length {
		int value();
	}
	

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Valid {
		Class<? extends Validate> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Auto {
		public static enum Type {
			CreateDate, CreateDateTime, ModifyDate, ModifyDateTime
		};

		Type value();
	}

	private static void checkWholeNumber(Object o) {
		if (o instanceof Short || o instanceof Byte || o instanceof Integer
				|| o instanceof Long)
			return;
		throw new RuntimeException(o + " is not a Whole Number type");
	}

	public static <T extends Mdo<?>> T instance(Class<T> t) {
		try {
			return t.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected transient Mdo_X<T> mdoX;

	public Mdo() {

	}

	public Mdo(Mdo_X<T> mdoX) {
		this();
		this.mdoX = mdoX;
		
	}

	@SuppressWarnings("unchecked")
	public boolean auth(String passwordFieldname, String password)
			throws AppException, Exception {
		return mdoX.auth((T) this, passwordFieldname, password);
	}

	/**
	 * 将本Dao的所有属性复制到另一个Dao中。
	 * 
	 * 注意：对于复杂Dao，使用此方法需要特别小心。因为可能无法处理复杂属性。
	 * 
	 * @param mdo
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void copyTo(Mdo<?> mdo) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			Object value = mdoField.getMethod.invoke(this, new Object[] {});
			if (value instanceof Mdo[]) {
				Mdo<?>[] daos = (Mdo[]) value;
				List<Mdo<?>> list = new LinkedList<Mdo<?>>();
				for (Mdo<?> dao : daos)
					list.add(dao);
				mdoField.setMethod.invoke(mdo, new Object[] { list });
			} else
				mdoField.setMethod.invoke(mdo, new Object[] { value });
		}
	}

	@SuppressWarnings("unchecked")
	public T create() throws AppException, Exception {
		if (exists())
			throw new UniqueConstraintException(this);

		return this.mdoX.create((T) this);
	}

	@SuppressWarnings("unchecked")
	public T createIfNotExist() throws AppException, Exception {
		if (!exists())
			return this.mdoX.create((T) this);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public void createOrUpdate() throws AppException, Exception {
		this.mdoX.createOrUpdate((T) this);
	}

	@SuppressWarnings("unchecked")
	public void delete() throws AppException, Exception {
		this.mdoX.delete((T) this);
	}

	public boolean equals(MdoMap mdoMap) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		for (Map.Entry<String, Object> entry : mdoMap.entrySet()) {
			if (!entry.getValue().equals(fieldValue(entry.getKey())))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean exists() throws AppException, Exception {
		return mdoX.exists((T) this);
	}

	public String fieldTip(String fieldName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String title = fieldTitle(fieldName);
		return title + ' ' + fieldValue(fieldName);
	}

	@SuppressWarnings("unchecked")
	public String fieldTitle(String name) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Field field = MdoReflector.getField((Class<T>) this.getClass(), name);
		Title fieldTitle = field.getAnnotation(Title.class);
		if (fieldTitle == null)
			return name;
		return fieldTitle.value();
	}

	/**
	 * 根据指定的属性名name返回该属性的值
	 * 
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object fieldValue(String name) {
		return MdoReflector.getFieldValue(this, name);
	}

	public void fieldValue(String name, Object v)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		MdoReflector.setFieldValue(this, name, v);
	}

	public void fieldValue(String name, String v)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		MdoReflector.setFieldValue(this, name, v);
	}

	public float fieldValueF(String name) {
		Object o = MdoReflector.getFieldValue(this, name);
		if (o instanceof Float)
			return ((Float) o).floatValue();
		else
			return Float.valueOf(String.valueOf(o));
	}

	public int fieldValueI(String name) {
		Object o = MdoReflector.getFieldValue(this, name);
		checkWholeNumber(o);
		return ((Number) o).intValue();
	}

	//
	// public static String stringArrayToString(String[] ss) {
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < ss.length; i++)
	// sb.append(ss[i]).append("::::");
	// return sb.toString();
	// }

	public long fieldValueL(String name) {
		Object o = MdoReflector.getFieldValue(this, name);
		checkWholeNumber(o);
		return ((Number) o).longValue();
	}

	public void filledBy(Map<String, String> map) throws MdoBuildException {
		MdoField[] mdoFields = getMdoFields();
		for (MdoField attr : mdoFields) {
			String value = map.get(attr.name);
			Object v = value;
			if (value == null)
				continue;
			try {
				v = StringUtils.toObject(value, attr.type);
				
				attr.setMethod.invoke(this, new Object[] { v });
			} catch (IllegalArgumentException e) {
				throw new MdoBuildException(e);
			} catch (IllegalAccessException e) {
				throw new MdoBuildException(e);
			} catch (InvocationTargetException e) {
				throw new MdoBuildException(e);
			}
		}
	}

	/**
	 * 获取对象在LDAP中的DN
	 * 
	 * @param baseDN
	 *            给定的Base DN
	 * @return
	 * @throws NullKeyException
	 */
	// public String dn(String baseDN) throws NullKeyException {
	// return selfDN() + ',' + fatherDN(baseDN);
	// }
	/**
	 * 从一个MdoMap中截取信息，填充到本对象的对应字段
	 * 
	 * @param mdoMap
	 * @throws MdoBuildException
	 */
	public void filledBy(MdoMap mdoMap) {
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			Object value = mdoMap.get(mdoField.name);
			if (value == null)
				continue;
			try {
				mdoField.setMethod.invoke(this, new Object[] { value });
			} catch (Exception e) {
				throw new MdoBuildException(e);
			}
		}
	}

	public void filledByAutoCreate() {
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mf : mdoFields) {
			if (mf.autoType == null)
				continue;
			String v = null;
			if (mf.autoType == Auto.Type.CreateDate)
				v = CalendarUtil.getLocalDate();
			else if (mf.autoType == Auto.Type.CreateDateTime)
				v = CalendarUtil.getLocalDateTime();
			else
				continue;
			try {
				mf.setMethod.invoke(this, v);
			} catch (Exception e) {
				throw new MdoBuildException(e);
			}
		}
	}

	public void filledBy(HttpServletRequest req, String srcCharset,
			String destCharset) {
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			String s = req.getParameter(mdoField.name);
			if (s == null)
				continue;
			try {
				Object obj = convertObject(mdoField.type, s, srcCharset,
						destCharset);
				mdoField.setMethod.invoke(this, new Object[] { obj });
			} catch (Exception e) {
				throw new MdoBuildException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public MdoField[] getMdoFields() {
		return MdoReflector.getMdoFields((Class<T>) this.getClass());
	}

	/**
	 * 仅用于LDAP中的Object Class指定。注意这可不是Java里的class.
	 * 
	 * @return
	 */
	// public abstract String[] objectClasses();
	/**
	 * 判断fieldname是否是关键字段。
	 * 
	 * @param fieldname
	 * @return
	 */
	public boolean isKeyField(String fieldname) {
		String[] keyNames = keyNames();
		for (String keyName : keyNames)
			if (keyName.equals(fieldname))
				return true;
		return false;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	//
	// Some method about store
	//
	// ////////////////////////////////////////////////////////////////////////////////

	public abstract String[] keyNames();

	public String keySQL() throws NullKeyException, MdoBuildException {
		String[] keyNames = keyNames();
		StringBuffer sb = new StringBuffer();
		sb.append("1=1");
		
		for(String keyName : keyNames) {
			Object value = MdoReflector.getFieldValue(this, keyName);
			if (value == null)
				throw new NullKeyException(keyName);
			String sql = keyName + "="
					+ RdbmsSession.getSQLSafeValue(value, value.getClass());
			sb.append(" AND ").append(sql);
		}
		
		return sb.toString();
	}

	public String keyTip() {
		try {
			String key = this.keyNames()[0];
			String title = fieldTitle(key);
			return title + ' ' + fieldValue(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T load() throws AppException, Exception {
		return this.mdoX.load((T) this);
	}

	@SuppressWarnings("unchecked")
	public Class<T> mdoClass() {
		return (Class<T>) getClass();
	}

	public abstract String table();

	public void throwExists() throws AppException, Exception {
		if (exists())
			throw new UniqueConstraintException(this);
	}

	public JSONObject toJson() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, JSONException {
		JSONObject jo = new JSONObject();
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			Object value = MdoReflector.getFieldValue(this, mdoField.name);
			jo.put(mdoField.name, value);
		}
		return jo;
	}

	/**
	 * 定义自身的DN
	 * 
	 * @return
	 * @throws NullKeyException
	 */
	// public abstract String selfDN() throws NullKeyException;
	/**
	 * 定义自身的上级DN
	 * 
	 * @param baseDN
	 * @return
	 * @throws NullKeyException
	 */
	// public abstract String fatherDN(String baseDN) throws NullKeyException;
	/**
	 * 返回一个MdoMap，MdoMap里的Key和Value分别是本对象的<b>关键</b>属性和值。
	 * 
	 * @see Mdo#toMap()
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NullKeyException
	 */
	public MdoMap toKeyMdoMap() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NullKeyException {
		MdoMap mdoMap = new MdoMap();
		String[] keyNames = keyNames();
		if (keyNames == null)
			throw new NullKeyException(this.getClass().getName());
		for (String keyName : keyNames) {
			Object value = MdoReflector.getFieldValue(this, keyName);
			if (value == null)
				throw new NullKeyException(keyName);
			mdoMap.put(keyName.toLowerCase(), value);
		}
		return mdoMap;
	}

	/**
	 * 返回一个Map，Map里的Key和Value分别是本对象的属性和值。
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Map<String, Object> toMap() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Map<String, Object> map = new HashMap<String, Object>();
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			Object value = MdoReflector.getFieldValue(this, mdoField.name);
			map.put(mdoField.name, value);
		}
		return map;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	//
	// Annotations
	//
	// ////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * 返回一个MdoMap，MdoMap里的Key和Value分别是本对象的属性和值。
	 * 
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MdoMap toMdoMap() {
		MdoMap mdoMap = new MdoMap();
		MdoField[] mdoFields = getMdoFields();
		try {
			for (MdoField mdoField : mdoFields) {
				Object value = MdoReflector.getFieldValue(this, mdoField.name);
				if (value == null)
					continue;
				mdoMap.put(mdoField.name, value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mdoMap;
	}

	@Override
	public String toString() {
		return toString(" ");
	}

	/**
	 * 以sepStr为分隔符，返回本对象的可视化字符串。用于DEBUG非常方便。
	 * 
	 * @param sepStr
	 * @return
	 */
	public String toString(String sepStr) {
		StringBuffer sb = new StringBuffer();
		MdoField[] mdoFields = getMdoFields();
		for (MdoField mdoField : mdoFields) {
			try {
				Object value = mdoField.getMethod.invoke(this, new Object[0]);
				sb.append(mdoField.name).append('=').append(value).append(
						sepStr);
			} catch (Exception e) {
				return e.getMessage();
			}
		}
		return sb.toString();
	}

	public List<String> toStringList() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<String> list = new LinkedList<String>();
		Collections.addAll(list, toStrings());
		return list;
	}

	public String[] toStrings() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		MdoField[] mdoFields = getMdoFields();
		String[] ss = new String[mdoFields.length];
		for (int i = 0; i < ss.length; i++) {
			Object v = fieldValue(mdoFields[i].name);
			if (v == null)
				ss[i] = "";
			else
				ss[i] = String.valueOf(v);
		}
		return ss;
	}

	@SuppressWarnings("unchecked")
	public T update(MdoMap mdoMap) throws AppException, Exception {
		return this.mdoX.update((T) this, mdoMap);
	}

	@SuppressWarnings("unchecked")
	public T update(String fieldFormat, Object... values) throws AppException,
			Exception {
		this.mdoX.update((T) this, fieldFormat, values);
		return (T) this;
	}

	// ///////////////////////////////////////////////////////////////////////
	private static String charConvert(String s, String srcCharset,
			String destCharset) throws UnsupportedEncodingException {
		if (destCharset == null)
			return s;
		if (srcCharset == null)
			return new String(s.getBytes(), destCharset);
		return new String(s.getBytes(srcCharset), destCharset);
	}

	@SuppressWarnings("unchecked")
	public static Object convertObject(Class<?> type, String s,
			String srcCharset, String destCharset)
			throws UnsupportedEncodingException {
		Object arg = null;
		if (type == String.class) {
			arg = charConvert(s, srcCharset, destCharset);
		} else if (type == StringBuffer.class) {
			arg = new StringBuffer(charConvert(s, srcCharset, destCharset));
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

}
