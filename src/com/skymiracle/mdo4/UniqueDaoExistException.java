package com.skymiracle.mdo4;

import java.lang.reflect.InvocationTargetException;

import com.skymiracle.sor.exception.AppException;

public class UniqueDaoExistException extends AppException {

	private static final long serialVersionUID = 7649418181279189886L;

	private Dao dao;
	
	public UniqueDaoExistException(Dao dao) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(dao.keyTip() +" 已经存在，不可重复添加");
		this.dao = dao;
	}
	
	public UniqueDaoExistException(Dao dao, String field) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(dao.fieldTip(field) +" 已经存在，不可重复添加");
		this.dao = dao;
	}
	
	public Dao getDao() {
		return this.dao;
	}

	
}
