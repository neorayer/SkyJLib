package com.skymiracle.mdo4.cache;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.NullKeyException;

public class DaoCacheNo implements DaoCache {

	public void clear() {
	

	}

	public Dao get(Class<? extends Dao> clazz, String key) {

		return null;
	}

	public Dao get(Dao dao) throws NullKeyException, DaoBuildException {

		return null;
	}

	public long getCount(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) {

		return -1;
	}

	public long getGetCount() {
		return 0;
	}

	public long getHitCount() {
		return 0;
	}

	public float getHitRate() {

		return 0;
	}

	public void put(Dao dao) throws NullKeyException, DaoBuildException {

	}

	public void putCount(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter, long count) {

	}

	public void remove(Class<? extends Dao> clazz, String key) {

	}

	public void remove(Dao dao) throws NullKeyException, DaoBuildException {

	}

	public void remove(Dao[] daos) throws NullKeyException, DaoBuildException {

	}

	public void remove(Class<? extends Dao> clazz) {

	}

	public void removeCount(Class<? extends Dao> clazz) {

	}

	public void rset() {

	}

}
