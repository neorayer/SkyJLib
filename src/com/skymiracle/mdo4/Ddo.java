package com.skymiracle.mdo4;

import java.util.List;

public abstract class Ddo<T extends Dao, ST extends IDaoServ<T>> extends Dao{

	public ST serv;
	
	Class<T> daoClass ;
	
	public Ddo() throws Exception{
		this.daoClass = (Class<T>) getClass();
		this.serv = newServ();
		setDaoStorage();
	}
	
	public abstract ST newServ();

	public abstract void setDaoStorage() throws Exception;
	
	public class DaoServ implements IDaoServ<T>{

		public  List<T> findAll() throws DaoStorageException {
			return daoStorage.getDaos(daoClass);
		}

	}

	
}
