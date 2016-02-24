package com.skymiracle.mdo4;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.skymiracle.logger.*;
import com.skymiracle.reflect.ReflectTools;

public class BeanAttrPool {

	/**
	 * mappings 是个静态Map，它将cache住所有已经反射过的类的方法，这样下次调用的时候将不需要再次反射了。
	 */
	private static Map<Class, BeanAttrPool> mappings = new HashMap<Class, BeanAttrPool>();

	public class BeanAttr {

		public final String fieldname;

		public final Method getMethod;

		public final Method setMethod;

		public final Field field;

		public final Class<? extends Object>  type;

		public boolean isPrimary = false;

		private BeanAttr(String fieldname, Method getMethod,
				Method setMethod, Field field, boolean isPrimary, Class type) {
			this.fieldname = fieldname;
			this.getMethod = getMethod;
			this.setMethod = setMethod;
			this.field = field;
			this.isPrimary = isPrimary;
			this.type = type;
		}
	}
	
	private final Map<String,BeanAttr> attrMap = new HashMap<String,BeanAttr>() {
		@Override
		public BeanAttr put(String key, BeanAttr attr) {
			return super.put(key.toLowerCase(), attr);
		}

		@Override
		public BeanAttr get(Object key) {
			return super.get(key.toString().toLowerCase());
		}
		
	};

	public static BeanAttrPool getInstance(Class source) {
		BeanAttrPool map = mappings.get(source);
		if (map == null)
			mappings.put(source, map = new BeanAttrPool(source));
		return map;
	}

	public static BeanAttr[] getAttrs(Class source) {
		return getInstance(source).getAttrs();
	}


	private boolean isDaoPrimaryField(Dao dao, String fieldname) {
		String[] keyNames = dao.keyNames();
		if (keyNames == null)
			return false;
		for (String keyName : keyNames) {
			if (keyName.equalsIgnoreCase(fieldname)) {
				return true;
			}
		}
		return false;
	}

	private BeanAttrPool(Class source) {
		List<BeanAttr> temp = new LinkedList<BeanAttr>();
		Method[] setMethods = ReflectTools.getSetterMethods(source);
		for (Method setMethod: setMethods) {
			String setMethodName = setMethod.getName();
			String fieldname = setMethodName.substring(3);
			
			Field field = ReflectTools.getField(source, Object.class, fieldname);
			
			if (field == null)
				continue;
			if(field != null && "daoStorage".equalsIgnoreCase(field.getName()))
				continue;
			
			Method getMethod = null;
			boolean isPrimary = false;
			try {
				getMethod = source.getMethod("get" + fieldname,
						new Class[0]);

				if (ReflectTools.isHeritate(source, Dao.class)) {
					try {
						Dao dao = (Dao) source.newInstance();
						isPrimary = isDaoPrimaryField(dao, fieldname);
					} catch (InstantiationException ie) {
						isPrimary = false;
					}
				}
			} catch (Exception e) {
				Logger.error("", e);
				continue;
			}

			BeanAttr attr = new BeanAttr(fieldname
					.toLowerCase(), getMethod, setMethod, field,
					isPrimary, getMethod.getReturnType());
			temp.add(attr);
		}
		this.attrMap.clear();
		for(BeanAttr attr: temp) {
			this.attrMap.put(attr.fieldname, attr);
		}
	}

	public BeanAttr[] getAttrs() {
		return this.attrMap.values().toArray(new BeanAttr[0]);
	}

	public static Method getGetterMethod(Class type, String fieldname) {
		return getInstance(type).attrMap.get(fieldname).getMethod;
	}

	public static Method getSetterMethod(Class type, String fieldname) {
		return getInstance(type).attrMap.get(fieldname).setMethod;
	}

	public static Class getType(Class type, String fieldname) {
		return getInstance(type).attrMap.get(fieldname).type;
	}
	

	public static Field getField(Class type, String fieldname)  {
		return getInstance(type).attrMap.get(fieldname).field;
	}
	

	public static BeanAttr getAttr(Class type, String fieldname)  {
		return getInstance(type).attrMap.get(fieldname);
	}

	public static Object getFieldValue(Dao dao, String fieldname)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return getGetterMethod(dao.getClass(), fieldname).invoke(dao,
				new Object[0]);
	}

	public static void setFieldValue(Dao dao, String fieldname, Object value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		getSetterMethod(dao.getClass(), fieldname).invoke(dao,
				new Object[] { value });
	}

}
