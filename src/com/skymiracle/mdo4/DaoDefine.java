package com.skymiracle.mdo4;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * DaoDefine 用于定义com.skymiracle.mdo4.Dao的继承类的属性。 如
 * len=64，表示当此字段为String类型时，在数据库中的最大长度为varchar(64)
 * 
 * 
 * @see {@link #Dao}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DaoDefine {
	int len() default 255; // 表示当此字段为String类型时，在数据库中的最大长度

	String title() default "Undefined Title"; // 字段的显示标题

	boolean isDate() default false; // 当类型为String时，此类型是个Date格式

	boolean isDateTime() default false; // 当类型为String时，此类型是个DateTime格式
	
	String vf() default ""; //校验名
}
