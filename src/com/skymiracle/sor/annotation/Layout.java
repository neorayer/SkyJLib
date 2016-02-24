package com.skymiracle.sor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Layout {
	String value();
	
	public static final String NOT = "NOT";
	
	public static final String portal = "portal";
}
