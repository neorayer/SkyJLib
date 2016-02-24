package com.skymiracle.mdo4;

import java.util.List;

import com.skymiracle.sor.exception.AppException;

/**
 * DaoStorage的意思是"域对象存储器”。相当于Hibernate里的一个“通用的数据访问对象”。
 * 
 * 
 * @author skymiracle
 * 
 * @see {@link RdbmsDaoStorage}
 * @see {@link XmlDaoStorage}
 * @see {@link LdapDaoStorage}
 */
public interface DaoStorage {

	public boolean auth(Dao dao, String passwordFieldname, String password)
			throws DaoStorageException, NullKeyException, AppException,
			Exception;

	public void empty(Class<? extends Dao> daoClass) throws DaoStorageException;

	public void delDao(Dao dao) throws DaoStorageException, DaoBuildException,
			NullKeyException, NotEmptyException;

	public <T extends Dao> void delDao(T[] daos) throws DaoStorageException,
			DaoBuildException, NullKeyException, NotEmptyException;

	public <T extends Dao> void delDao(List<T> daos)
			throws DaoStorageException, DaoBuildException, NullKeyException;

	public void loadDao(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException, KeyNotExistException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass)
			throws DaoStorageException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet) throws DaoStorageException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String orderBy, boolean isASC)
			throws DaoStorageException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy, boolean isASC)
			throws DaoStorageException;

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy,
			boolean isASC, long limitBegin, long limitCount)
			throws DaoStorageException;

	public <T extends Dao> T addDao(T dao) throws DaoStorageException,
			NullKeyException;

	public void addDao(Dao[] daos) throws DaoStorageException, NullKeyException;

	public <T extends Dao> T modDao(T dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException,
			KeyNotExistException;

	public void modDao(Dao[] daos, DaoAttrSet[] daoAttrSets)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException;

	public void modDao(Class<? extends Dao> daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException;

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException;

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) throws DaoStorageException;

	public long sum(Class<? extends Dao> daoClass, String fieldName,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException;

	public boolean existDao(Dao dao) throws DaoStorageException,
			NullKeyException, DaoBuildException;

	public void delDao(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			NotEmptyException;

	public void incDao(Class<? extends Dao> daoClass, DaoAttrSet condition,
			String fieldName, int value) throws DaoStorageException;

	public <T extends Dao> DList<T> getDaosByField(Class<T> daoClass,
			String field, String value) throws DaoStorageException;

	public <T extends Dao> T getDaoByField(Class<T> daoClass, String field,
			String value) throws DaoStorageException;
}
