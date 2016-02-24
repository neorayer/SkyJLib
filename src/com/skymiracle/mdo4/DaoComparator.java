package com.skymiracle.mdo4;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * 一个通用的DomainObject比较器，常用于Collection<Dao>类型的的排序。
 * 采用DaoComparator，可以大大的简化在JVM中排序代码的数量。避免了写一大堆匿名类的尴尬。
 * 
 * 有2点需要注意：
 * 1、由于采用类反射的方式实现属性值的获取，因此效率可能略有影响。
 * 2、value的比较采用字符比较方式，这显然是愚蠢而的做法，希望看到此处的程序员提出改动方案。
 * 改动时必须注意不能影响被其应用的项目。
 * 
 * @author skymiracle
 *
 */
public class DaoComparator<T extends Dao> implements Comparator<T> {
	String orderby;

	boolean isASC;

	int flag = 1;

	/**
	 * 
	 * @param orderby
	 * @param isASC
	 */
	public DaoComparator(String orderby, boolean isASC) {
		this.orderby = orderby;
		this.isASC = isASC;
		this.flag = isASC ? 1 : -1;
	}

	public int compare(T arg0, T arg1) {
		T dao0 =  arg0;
		T dao1 =  arg1;
		try {
			Object value0 = BeanAttrPool.getFieldValue(dao0, this.orderby);
			Object value1 = BeanAttrPool.getFieldValue(dao1, this.orderby);
			if (dao0 instanceof Comparable)
				return value0.toString().compareTo(value1.toString())
						* this.flag;
			return 0;
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (IllegalAccessException e) {
			return 0;
		} catch (InvocationTargetException e) {
			return 0;
		}
	}

}
