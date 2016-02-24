package com.skymiracle.mdo4.testCase;

import java.lang.reflect.InvocationTargetException;

import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.DaoStorageException;
import com.skymiracle.mdo4.KeyNotExistException;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo4.RdbmsDaoStorage;
import com.skymiracle.mdo4.trans.TransDefine;

public interface ICaseService {
	@TransDefine
	public void addDomain(Domain domain) throws DaoStorageException,
			NullKeyException;

	@TransDefine
	public void addUser(Domain domain, User user)
			throws IllegalArgumentException, DaoStorageException,
			IllegalAccessException, InvocationTargetException, NullKeyException;

	@TransDefine
	public void addUserFailed(Domain domain, User user) throws Exception;

	public long getUserCount(Domain domain) throws DaoStorageException;

	public long getUserCountFromDomain(Domain domain)
			throws DaoStorageException, NullKeyException, DaoBuildException,
			KeyNotExistException;

	public RdbmsDaoStorage getStorage();

	public Domain getDomain(String dc) throws DaoStorageException, NullKeyException, DaoBuildException;
}
