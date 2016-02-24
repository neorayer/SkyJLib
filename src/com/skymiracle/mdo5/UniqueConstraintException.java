package com.skymiracle.mdo5;

import java.lang.reflect.InvocationTargetException;

import com.skymiracle.sor.exception.AppException;

public class UniqueConstraintException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5469400638214298928L;

	private Mdo<?> mdo;
	
	public UniqueConstraintException(Mdo<?> mdo) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(mdo.keyTip() +" 已经存在，不可重复添加");
		this.mdo = mdo;
	}
	
	public UniqueConstraintException(Mdo<?> mdo, String field) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(mdo.fieldTip(field) +" 已经存在，不可重复添加");
		this.mdo = mdo;
	}
	
	public Mdo<?> getDao() {
		return this.mdo;
	}
}
