package com.skymiracle.mdo4;

import java.util.List;

	public interface IDaoServ<T> {
		public  List<T> findAll() throws DaoStorageException;
	}