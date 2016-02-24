package com.skymiracle.mdo4.cache;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.NullKeyException;

public interface DaoCache {

	public abstract long getHitCount();

	public abstract long getGetCount();

	public abstract float getHitRate();

	public abstract void clear();

	public abstract void rset();

	public abstract Dao get(Class<? extends Dao> clazz, String key);

	public abstract Dao get(Dao dao) throws NullKeyException, DaoBuildException;

	public abstract void put(Dao dao) throws NullKeyException,
			DaoBuildException;

	public abstract void remove(Class<? extends Dao> clazz, String key);

	public abstract void remove(Dao dao) throws NullKeyException,
			DaoBuildException;

	public abstract void remove(Dao[] daos) throws NullKeyException,
			DaoBuildException;

	public abstract void remove(Class<? extends Dao> clazz);

	public abstract long getCount(Class<? extends Dao> daoClass,
			DaoAttrSet daoAttrSet, String filter);

	public abstract void putCount(Class<? extends Dao> daoClass,
			DaoAttrSet daoAttrSet, String filter, long count);

	public abstract void removeCount(Class<? extends Dao> clazz);

}