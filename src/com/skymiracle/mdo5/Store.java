package com.skymiracle.mdo5;

import com.skymiracle.sor.exception.AppException;

/**
 * MdoStorage的意思是"域对象存储器”。相当于Hibernate里的一个“通用的数据访问对象”。
 * 
 */
public interface Store {

	public boolean auth(Mdo<?> mdo, String passwordFieldname, String password)
			throws AppException, Exception;

	public long count(Class<? extends Mdo<?>> mdoClass) throws AppException,
			Exception;

	public long count(Class<? extends Mdo<?>> mdoClass, MdoMap mdoMap)
			throws AppException, Exception;

	public long count(Class<? extends Mdo<?>> mdoClass, MdoMap mdoMap,
			String filter) throws AppException, Exception;

	public long count(Class<? extends Mdo<?>> mdoClass, String fieldsFormat,
			Object... values) throws AppException, Exception;

	public <T extends Mdo<T>> T create(T mdo) throws AppException, Exception,
			NullKeyException;

	public <T extends Mdo<T>> void create(T[] mdos) throws AppException,
			Exception;

	public <T extends Mdo<T>> T createOrUpdate(T mdo) throws AppException,
			Exception, MdoBuildException;

	public <T extends Mdo<T>> void delete(Class<T> mdoClass, MdoMap mdoMap)
			throws AppException, Exception;

	public <T extends Mdo<T>> void delete(MList<T> mdos) throws AppException,
			Exception;

	public <T extends Mdo<T>> void delete(T mdo) throws AppException, Exception;

	public <T extends Mdo<T>> void delete(T[] mdos) throws AppException,
			Exception;

	public <T extends Mdo<T>> void deleteAll(Class<T> mdoClass)
			throws AppException, Exception;

	public <T extends Mdo<T>> boolean exists(T mdo) throws AppException,
			Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap)
			throws AppException, Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter) throws AppException, Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String orderBy, boolean isASC) throws AppException, Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter, String orderBy, boolean isASC) throws AppException,
			Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass, MdoMap mdoMap,
			String filter, String orderBy, boolean isASC, long limitBegin,
			long limitCount) throws AppException, Exception;

	public <T extends Mdo<T>> MList<T> find(Class<T> mdoClass,
			String fieldsFormat, Object... values) throws AppException,
			Exception;

	public <T extends Mdo<T>> MList<T> findAll(Class<T> mdoClass)
			throws AppException, Exception;

	public void inc(Mdo<?> mdo, String fieldName, int value)
			throws AppException, Exception;

	public <T extends Mdo<T>> T load(T mdo) throws AppException, Exception,
			NotExistException;

	public <T extends Mdo<T>> long sum(Class<T> mdoClass, String fieldName,
			MdoMap mdoMap, String filter) throws AppException, Exception;

	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			MdoMap condition) throws AppException, Exception, NotExistException;

	public <T extends Mdo<T>> void update(Class<T> mdoClass, MdoMap dataMdoMap,
			StringBuffer conditionSQL) throws AppException, Exception,
			NotExistException;

	public <T extends Mdo<T>> T update(T mdo, MdoMap mdoMap)
			throws AppException, Exception, NotExistException;

	public <T extends Mdo<T>> T update(T mdo, String fieldsFormat,
			Object... values) throws AppException, Exception, NotExistException;
}
