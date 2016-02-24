package com.skymiracle.mdo4.cache;

import java.util.List;

import com.skymiracle.mdo4.Dao;

public interface DaoMemCache {

	public long getHitCount();

	public long getGetCount();

	public float getHitRate();

	public void clear();

	public void rset();
	
	public boolean load(Dao mdo);

	public boolean put(Dao mdo);

	public void remove(Dao mdo);

	public void remove(Dao[] mdos);
	
	public void remove(List<? extends Dao> daosList);

	public void initialize();

}