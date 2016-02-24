package com.skymiracle.mdo4.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.NullKeyException;

public class DaoCacheJVM implements DaoCache {
	
	private Map<Class<? extends Dao>, Map<String, Dao>> cacheDataMap = new ConcurrentHashMap<Class<? extends Dao>, Map<String, Dao>>();
	private Map<Class<? extends Dao>, Map<String, Long>> cacheCountMap = new ConcurrentHashMap<Class<? extends Dao>, Map<String, Long>>();
	

	private long hitCount = 0;
	
	private long getCount = 0;


	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#getHitCount()
	 */
	public long getHitCount() {
		return this.hitCount;
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#getGetCount()
	 */
	public long getGetCount() {
		return this.getGetCount();
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#getHitRate()
	 */
	public float getHitRate() {
		return (float) hitCount / getCount;
	}
	
	@Override
	public String toString() {
		String s = "DaoCache: " 
			+ hitCount + "/" + getCount 
			+ " " + getHitRate();
		return s;
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#clear()
	 */
	public void clear() {
		cacheDataMap.clear();
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#rset()
	 */
	public void rset() {
		clear();
		hitCount = 0;
		getCount = 0;
	}

	private synchronized void incGetCount() {
		getCount ++;
	}
	
	private synchronized void incHitCount() {
		hitCount ++;
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#get(java.lang.Class, java.lang.String)
	 */
	public Dao get(Class<? extends Dao> clazz, String key) {
		incGetCount();
		Dao dao = getL2DataMap(clazz).get(key);
		log("GET", clazz, key, dao != null);
		return dao;
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#get(com.skymiracle.mdo4.Dao)
	 */
	public Dao get(Dao dao) throws NullKeyException, DaoBuildException {
		return get(dao.getClass(), dao.keySQL());
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#put(com.skymiracle.mdo4.Dao)
	 */
	public void put(Dao dao) throws NullKeyException, DaoBuildException {
		Class<? extends Dao> clazz = dao.getClass();
		String key = dao.keySQL();
		log("PUT", clazz, key, false);
		getL2DataMap(clazz).put(key, dao);
	}
	

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#remove(java.lang.Class, java.lang.String)
	 */
	public void remove(Class<? extends Dao> clazz, String key) {
		//Dao dao = getL2Map(clazz).get(key);
		getL2DataMap(clazz).remove(key);
		log("REMOVE", clazz, key, false);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#remove(com.skymiracle.mdo4.Dao)
	 */
	public void remove(Dao dao) throws NullKeyException, DaoBuildException {
		remove(dao.getClass(), dao.keySQL());
		
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#remove(com.skymiracle.mdo4.Dao[])
	 */
	public void remove(Dao[] daos) throws NullKeyException, DaoBuildException {
		for(Dao dao : daos)
			remove(dao);
	}
	
	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#remove(java.lang.Class)
	 */
	public void remove(Class<? extends Dao> clazz) {
		log("REMOVE", clazz, "", false);
		cacheDataMap.remove(clazz);
		cacheCountMap.remove(clazz);
	}

	private Map<String, Dao> getL2DataMap(Class<? extends Dao> clazz) {
		Map<String, Dao> l2Map = cacheDataMap.get(clazz);
		if (l2Map == null) {
			l2Map = new ConcurrentHashMap<String, Dao>();
			cacheDataMap.put(clazz, l2Map);
		}
		return l2Map;
	}
	
	private Map<String, Long> getL2CountMap(Class<? extends Dao> clazz) {
		Map<String, Long> l2Map = cacheCountMap.get(clazz);
		if (l2Map == null) {
			l2Map = new ConcurrentHashMap<String, Long>();
			cacheCountMap.put(clazz, l2Map);
		}
		return l2Map;
	}
	
	private void log(String act, Class<? extends Dao> clazz, String key, boolean isHit) {
		if (isHit)
			incHitCount();
		Logger.debug("DaoCache " + act + " [" + isHit + "] " + clazz.getName() + " key=[" + key + "]");
	}



	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#getCount(java.lang.Class, com.skymiracle.mdo4.DaoAttrSet, java.lang.String)
	 */
	public long getCount(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) {
		incGetCount();
		String key = (daoAttrSet == null ? "1 = 1" : daoAttrSet.toConditionSQL()) + (filter == null ?"" :" AND " + filter);
		 Map<String, Long> l2Map = getL2CountMap(daoClass);
		Long count = l2Map.get(key);
		if (count == null)
			return -1;
		log("COUNT", daoClass, key, true);
		return count;
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#putCount(java.lang.Class, com.skymiracle.mdo4.DaoAttrSet, java.lang.String, long)
	 */
	public void putCount(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter, long count) {
		String key = (daoAttrSet == null ? "1 = 1" : daoAttrSet.toConditionSQL()) + (filter == null ?"" :" AND " + filter);
		 Map<String, Long> l2Map = getL2CountMap(daoClass);
		 l2Map.put(key, count);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.mdo4.IDaoCache#removeCount(java.lang.Class)
	 */
	public void removeCount(Class<? extends Dao> clazz) {
		cacheCountMap.remove(clazz);
	}



}
