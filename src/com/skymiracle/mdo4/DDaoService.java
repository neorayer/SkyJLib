package com.skymiracle.mdo4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DDaoService<DaoType extends Dao> extends DaoService {

	private Class<DaoType> daoClass;

	public DDaoService(Class<DaoType> daoClass) {
		super();
		this.daoClass = daoClass;
	}

	public DaoType load(String... keyValues) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, DaoStorageException, NullKeyException,
			DaoBuildException {
		DaoType d = daoClass.newInstance();
		int i = 0;
		for (String key : d.keyNames()) {
			d.fieldValue(key, keyValues[i++]);
		}
		try {
			loadDao(d);
			return d;
		} catch (KeyNotExistException e) {
			return null;
		}
	}

	public List<DaoType> findAll() throws DaoStorageException {
		return getDaosList(daoClass);
	}

	public List<DaoType> find(DaoAttrSet das) throws DaoStorageException {
		return getDaosList(daoClass, das);
	}

	public List<DaoType> find(DaoAttrSet das, String filter)
			throws DaoStorageException {
		return getDaosList(daoClass, das, filter);
	}

	public List<DaoType> find(DaoAttrSet das, String filter, String orderBy,
			boolean isASC) throws DaoStorageException {
		return getDaosList(daoClass, das, filter, orderBy, isASC);
	}

	public List<DaoType> find(DaoAttrSet das, String filter, String orderBy,
			boolean isASC, int limitBegin, int limitCount)
			throws DaoStorageException {
		return getDaosList(daoClass, das, filter, orderBy, isASC, limitBegin,
				limitCount);
	}

	public List<DaoType> findPaged(DaoAttrSet das, String filter,
			String orderBy, boolean isASC, int pageNum, int countPerPage)
			throws DaoStorageException {
		return getDaosPaged(daoClass, das, filter, orderBy, isASC, pageNum,
				countPerPage);
	}

	public long sum(String fieldName) throws DaoStorageException {
		return sum(daoClass, fieldName, null, null);
	}

	public long sum(String fieldName, DaoAttrSet das, String filter)
			throws DaoStorageException {
		return sum(daoClass, fieldName, das, filter);
	}

	public DList<DaoType> findByField(String field, String value) throws DaoStorageException {
		return this.daoStorage.getDaosByField(daoClass, field, value);
	}

	public File exportCSV(File file, String charset)
			throws DaoStorageException, IllegalArgumentException, IOException,
			IllegalAccessException, InvocationTargetException {
		return exportCSV(file, charset, daoClass);
	}
	
}
