package com.skymiracle.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReflectTools {

	public static boolean isSimpleClass(Class clazz) {
		if (isMathClass(clazz))
			return true;
		else if (clazz == String.class)
			return true;
		else if (clazz == boolean.class)
			return true;
		return false;
	}

	public static boolean isMathClass(Class clazz) {
		if (clazz == byte.class)
			return true;
		else if (clazz == short.class)
			return true;
		else if (clazz == int.class)
			return true;
		else if (clazz == long.class)
			return true;
		else if (clazz == float.class)
			return true;
		else if (clazz == double.class)
			return true;
		if (clazz == Byte.class)
			return true;
		else if (clazz == Short.class)
			return true;
		else if (clazz == Integer.class)
			return true;
		else if (clazz == Long.class)
			return true;
		else if (clazz == Float.class)
			return true;
		else if (clazz == Double.class)
			return true;
		return false;
	}

	public static Method[] getGetterMethods(Class theClass, String pkgHead) {
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = theClass.getMethods();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (!method.getName().startsWith("get")
					&& !method.getName().startsWith("is"))
				continue;
			if (method.toString().indexOf(pkgHead) < 0)
				continue;
			if (method.getParameterTypes().length != 0)
				continue;

			methodList.add(method);
		}
		return methodList.toArray(new Method[0]);
	}

	public static Method[] getGetterMethods(Class theClass) {
		return getGetterMethods(theClass, "");
	}

	public static Method getGetterMethod(Class theClass, String attrName)
			throws Exception {
		Method[] methods = getGetterMethods(theClass);
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("get" + attrName)
					|| method.getName().equalsIgnoreCase("is" + attrName))
				return method;
		}
		throw new Exception("No such method. get" + attrName + "()");
	}

	public static Method getSetterMethod(Class theClass, String attrName)
			throws Exception {
		Method[] methods = getSetterMethods(theClass);
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("set" + attrName))
				return method;
		}
		throw new Exception("No such method. set" + attrName + "(xxx)");
	}

	public static Method[] getSetterMethods(Class theClass) {
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = theClass.getMethods();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (!method.getName().startsWith("set"))
				continue;
			if (method.getParameterTypes().length != 1)
				continue;
			methodList.add(method);
		}
		return methodList.toArray(new Method[0]);
	}

	/**
	 * TODO
	 * 
	 * @param class1
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static String[] getEnumConstant(Class clazz, String head)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = clazz.getDeclaredFields();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			if (!fieldName.startsWith(head))
				continue;
			list.add(fieldName.substring(head.length()));
		}
		return list.toArray(new String[0]);
	}

	public static Object invoke(Object obj, String methodName, Object[] args)
			throws Exception {
		Class clazz = obj.getClass();
		Method theMethod = null;
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				theMethod = method;
				break;
			}
		}
		if (theMethod == null) {
			throw new Exception("No suct Method. " + clazz.getName() + "."
					+ methodName + "(...)");
		}

		return theMethod.invoke(obj, args);
	}

	public static boolean isHeritate(Class<?> subClazz, Class<?> parentClazz) {
		try {
			subClazz.asSubclass(parentClazz);
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}
	public static <T> Field getField(Class<? extends T> clazz,
			Class<T> lastClazz, String fieldName) {
		List<Field> fields = getAllFields(clazz, lastClazz);
		for(Field field: fields)
			if (field.getName().equalsIgnoreCase(fieldName))
				return field;
		return null;
		
	}
	public static <T> List<Field> getAllFields(Class<? extends T> clazz,
			Class<T> lastClazz) {
		List<Field> allFields = new LinkedList<Field>();
		Field[] fs = clazz.getDeclaredFields();
		Collections.addAll(allFields, fs);

		if (clazz.equals(lastClazz))
			return allFields;

		Class superClazz = clazz.getSuperclass();

		if (superClazz.equals(lastClazz))
			return allFields;

		if (superClazz.equals(Object.class))
			return allFields;

		allFields.addAll(getAllFields(superClazz, lastClazz));
		return allFields;
	}

}
