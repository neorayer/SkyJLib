package com.skymiracle.mdo4.testCase;

import java.lang.reflect.InvocationTargetException;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.DaoService;
import com.skymiracle.mdo4.DaoStorageException;
import com.skymiracle.mdo4.KeyNotExistException;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo4.RdbmsDaoStorage;
import com.skymiracle.mdo4.trans.TransDefine;

public class CaseService extends DaoService implements ICaseService {

	@TransDefine
	public void addDomain(Domain domain) throws DaoStorageException,
			NullKeyException {
		addDao(domain);
	}
	
	@TransDefine
	public void addUser(Domain domain, User user)
			throws IllegalArgumentException, DaoStorageException,
			IllegalAccessException, InvocationTargetException, NullKeyException {
		user.setDc(domain.getDc());
		incDao(Domain.class, domain.toKeyDaoAttrSet(), "UserLocate", 1);
		addDao(user);
	}

	@TransDefine
	public void addUserFailed(Domain domain, User user) throws Exception {
		user.setDc(domain.getDc());
		incDao(Domain.class, domain.toKeyDaoAttrSet(), "UserLocate", 1);
		if (true)
			throw new Exception("Test Exception");
		addDao(user);
	}

	public long getUserCount(Domain domain) throws DaoStorageException {
		DaoAttrSet das = new DaoAttrSet();
		das.put("dc", domain.getDc());
		return this.count(User.class, das);
	}
	


	public long getUserCountFromDomain(Domain domain) throws DaoStorageException, NullKeyException, DaoBuildException, KeyNotExistException {
		loadDao(domain);
		return domain.getUserLocate();
	}

	public RdbmsDaoStorage getStorage() {
		return (RdbmsDaoStorage) super.getDaoStorage();
	}

	public Domain getDomain(String dc) throws DaoStorageException, NullKeyException, DaoBuildException {
		Domain domain = new Domain();
		domain.setDc(dc);
		try {
			loadDao(domain);
			return domain;
		}  catch (KeyNotExistException e) {
			return null;
		}
		
	}
}
