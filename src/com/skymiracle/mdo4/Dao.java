package com.skymiracle.mdo4;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;
import com.skymiracle.sql.SQLSession;

/**
 * 注意：此处的Dao的意思并非Data Access Object的意思。并不是Hibernate里的Dao。 它的正确意思是Domain Abstract
 * Object 域抽象对象。是MDO4框架的域模式中，所有与Domain对象的基类。 它本身不具备充血DomainObject的CRUD功能。
 * 与MDO3框架不同，MDO4框架采用典型的贫血模式，由DaoService或DaoStorage来完成CRUD工作。
 * 
 * 可以用DaoDefine定义继承类的属性。 如 len=64，表示当此字段为String类型时，在数据库中的最大长度为varchar(64)，例：
 * 
 * public Class User {
 * 
 * @DaoDefine(len=64) private String username; }
 * @author skymiracle
 * 
 */
public abstract class Dao {

	public abstract String[] keyNames();

	public abstract String table();

	protected DaoStorage daoStorage;

	public void setDaoStorage(DaoStorage daoStorage) {
		this.daoStorage = daoStorage;
	}

	/**
	 * DomainObject获得的关键字段的查询语句。
	 * 
	 * @return
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 */
	public String keySQL() throws NullKeyException, DaoBuildException {
		String[] keyNames = keyNames();
		StringBuffer sb = new StringBuffer();
		sb.append("1=1");
		for (int i = 0; i < keyNames.length; i++) {

			try {
				Object value = BeanAttrPool.getFieldValue(this, keyNames[i]);
				if (value == null)
					throw new NullKeyException();
				String sql = keyNames[i] + "="
						+ SQLSession.getSQLSafeValue(value, value.getClass());
				sb.append(" AND ").append(sql);
			} catch (IllegalArgumentException e) {
				throw new DaoBuildException(e);
			} catch (IllegalAccessException e) {
				throw new DaoBuildException(e);
			} catch (InvocationTargetException e) {
				throw new DaoBuildException(e);
			}
		}
		return sb.toString();
	}

	/**
	 * 仅用于LDAP中的Object Class指定。注意这可不是Java里的class.
	 * 
	 * @return
	 */
	public abstract String[] objectClasses();

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

	/**
	 * 获取对象在LDAP中的DN
	 * 
	 * @param baseDN
	 *            给定的Base DN
	 * @return
	 * @throws NullKeyException
	 */
	public String dn(String baseDN) throws NullKeyException {
		return selfDN() + ',' + fatherDN(baseDN);
	}

	/**
	 * 从一个DaoAttrSet中截取信息，填充到本对象的对应字段
	 * 
	 * @param attrSet
	 * @throws DaoBuildException
	 */
	public void fetchFromAttrSet(DaoAttrSet attrSet) throws DaoBuildException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value = attrSet.get(attrs[i].fieldname);
			if (value == null)
				continue;
			try {
				attrs[i].setMethod.invoke(this, new Object[] { value });
			} catch (IllegalArgumentException e) {
				throw new DaoBuildException(e);
			} catch (IllegalAccessException e) {
				throw new DaoBuildException(e);
			} catch (InvocationTargetException e) {
				throw new DaoBuildException(e);
			}
		}
	}

	public void fetchFromMap(Map<String, String> map) throws DaoBuildException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (BeanAttr attr : attrs) {
			String value = map.get(attr.fieldname);
			Object v = value;
			if (value == null)
				continue;
			try {
				if (attr.type.equals(StringBuffer.class)) {
					v = new StringBuffer(value);
				}
				attr.setMethod.invoke(this, new Object[] { v });
			} catch (IllegalArgumentException e) {
				throw new DaoBuildException(e);
			} catch (IllegalAccessException e) {
				throw new DaoBuildException(e);
			} catch (InvocationTargetException e) {
				throw new DaoBuildException(e);
			}
		}
	}
	public void fetchFromObjectMap (Map<String ,Object> map) throws DaoBuildException{
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (BeanAttr attr : attrs) {
			Object value = map.get(attr.fieldname);
			Object v = value;
			if (value == null)
				continue;
			try {
				attr.setMethod.invoke(this, new Object[] { v });
			} catch (IllegalArgumentException e) {
				throw new DaoBuildException(e);
			} catch (IllegalAccessException e) {
				throw new DaoBuildException(e);
			} catch (InvocationTargetException e) {
				throw new DaoBuildException(e);
			}
		}
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
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value;
			try {
				value = attrs[i].getMethod.invoke(this, new Object[0]);
			} catch (IllegalArgumentException e) {
				return e.getMessage();
			} catch (IllegalAccessException e) {
				return e.getMessage();
			} catch (InvocationTargetException e) {
				return e.getMessage();
			}
			sb.append(attrs[i].fieldname).append('=').append(value).append(
					sepStr);
		}
		return sb.toString();
	}

	/**
	 * 返回一个Map，Map里的Key和Value分别是本对象的属性和值。
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		try{
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value;
			value = attrs[i].getMethod.invoke(this, new Object[0]);
			map.put(attrs[i].fieldname, value);
		}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return map;
	}
	
	
	

	/**
	 * 定义自身的DN
	 * 
	 * @return
	 * @throws NullKeyException
	 */
	public abstract String selfDN() throws NullKeyException;

	/**
	 * 定义自身的上级DN
	 * 
	 * @param baseDN
	 * @return
	 * @throws NullKeyException
	 */
	public abstract String fatherDN(String baseDN) throws NullKeyException;

	/**
	 * 返回一个DaoAttrSet，DaoAttrSet里的Key和Value分别是本对象的<b>关键</b>属性和值。
	 * 
	 * @see Dao#toMap()
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NullKeyException
	 */
	public DaoAttrSet toKeyDaoAttrSet() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NullKeyException {
		DaoAttrSet daoAttrSet = new DaoAttrSet();
		String[] keyNames = keyNames();
		if (keyNames == null)
			throw new NullKeyException(this.getClass().getName());
		for (int i = 0; i < keyNames.length; i++) {
			Object value = BeanAttrPool.getFieldValue(this, keyNames[i]);
			if (value == null)
				throw new NullKeyException(keyNames[i]);
			daoAttrSet.put(keyNames[i].toLowerCase(), value);
		}
		return daoAttrSet;
	}

	/**
	 * 
	 * 返回一个DaoAttrSet，DaoAttrSet里的Key和Value分别是本对象的属性和值。
	 * 
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DaoAttrSet toDaoAttrSet() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		DaoAttrSet daoAttrSet = new DaoAttrSet();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value = attrs[i].getMethod.invoke(this, new Object[] {});
			if (value == null)
				continue;
			daoAttrSet.put(attrs[i].fieldname, value);
		}
		return daoAttrSet;
	}

	/**
	 * 将本Dao的所有属性复制到另一个Dao中。
	 * 
	 * 注意：对于复杂Dao，使用此方法需要特别小心。因为可能无法处理复杂属性。
	 * 
	 * @param dao
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void copyTo(Dao dao) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		for (int i = 0; i < attrs.length; i++) {
			Object value = attrs[i].getMethod.invoke(this, new Object[] {});
			if (value instanceof Dao[]) {
				Dao[] daos = (Dao[]) value;
				List<Dao> list = new ArrayList<Dao>();
				for (int di = 0; di < daos.length; di++)
					list.add(daos[di]);
				attrs[i].setMethod.invoke(dao, new Object[] { list });
			} else
				attrs[i].setMethod.invoke(dao, new Object[] { value });
		}
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
	public Object fieldValue(String name) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		return BeanAttrPool.getFieldValue(this, name);
	}

	public void fieldValue(String name, String v)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		BeanAttrPool.setFieldValue(this, name, v);
	}

	public long fieldValueL(String name) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object o = BeanAttrPool.getFieldValue(this, name);
		checkWholeNumber(o);
		return ((Number) o).longValue();
	}

	public int fieldValueI(String name) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object o = BeanAttrPool.getFieldValue(this, name);
		checkWholeNumber(o);
		return ((Number) o).intValue();
	}

	private static void checkWholeNumber(Object o) {
		if (o instanceof Short || o instanceof Byte || o instanceof Integer
				|| o instanceof Long)
			return;
		throw new RuntimeException(o + " is not a Whole Number type");
	}

	public String fieldTitle(String name) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Field field = BeanAttrPool.getField(this.getClass(), name);
		DaoDefine daoDefine = field.getAnnotation(DaoDefine.class);
		if (daoDefine == null)
			return name;
		return daoDefine.title();
	}

	public static String stringArrayToString(String[] ss) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++)
			sb.append(ss[i]).append("::::");
		return sb.toString();
	}

	public String keyTip() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String key = this.keyNames()[0];
		String title = fieldTitle(key);
		return title + ' ' + fieldValue(key);
	}

	public String fieldTip(String fieldName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String title = fieldTitle(fieldName);
		return title + ' ' + fieldValue(fieldName);
	}

	public List<String> toStringList() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<String> list = new LinkedList<String>();
		Collections.addAll(list, toStrings());
		return list;
	}

	public String[] toStrings() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		BeanAttr[] attrs = BeanAttrPool.getAttrs(this.getClass());
		String[] ss = new String[attrs.length];
		for (int i = 0; i < ss.length; i++) {
			Object v = fieldValue(attrs[i].fieldname);
			if (v == null)
				ss[i] = "";
			else
				ss[i] = String.valueOf(v);
		}
		return ss;
	}
	
	public void del() throws DaoStorageException, DaoBuildException, NullKeyException, NotEmptyException {
		this.daoStorage.delDao(this);
	}
}
