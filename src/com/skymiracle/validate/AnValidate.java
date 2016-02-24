package com.skymiracle.validate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnValidate {
	String vf() ;
}
