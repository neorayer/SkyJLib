package com.skymiracle.mdo4.confDao;

import java.lang.reflect.InvocationTargetException;

import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.DaoStorage;
import com.skymiracle.mdo4.DaoStorageException;
import com.skymiracle.mdo4.KeyNotExistException;
import com.skymiracle.mdo4.NullKeyException;

public class ConfDaoConfigure {

	private HasConfDao hasConfDao;

	private DaoStorage daoStorage;

	public ConfDaoConfigure(HasConfDao hasConfDao, DaoStorage daoStorage)
			throws DaoStorageException, NullKeyException, DaoBuildException {
		this.hasConfDao = hasConfDao;
		this.daoStorage = daoStorage;

		try {
			this.daoStorage.loadDao(this.hasConfDao.getConfDao());
		} catch (KeyNotExistException e) {
			this.daoStorage.addDao(this.hasConfDao.getConfDao());
		}
	}

	public void save() throws IllegalArgumentException, DaoStorageException,
			NullKeyException, DaoBuildException, KeyNotExistException,
			IllegalAccessException, InvocationTargetException {
		this.daoStorage.modDao(this.hasConfDao.getConfDao(), this.hasConfDao
				.getConfDao().toDaoAttrSet());
	}

}
