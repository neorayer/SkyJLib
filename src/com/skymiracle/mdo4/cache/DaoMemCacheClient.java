package com.skymiracle.mdo4.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoBuildException;
import com.skymiracle.mdo4.NullKeyException;


public class DaoMemCacheClient implements DaoMemCache {

	private SockIOPool pool = SockIOPool.getInstance();
	
	private MemCachedClient mcc = new MemCachedClient();

	
	public DaoMemCacheClient() {
		/*pool.setServers(new String[] { "10.1.1.188:11211" });
		pool.setWeights(new Integer[] { 3, 3, 2 });
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);*/
	}

	public void setServers(String[] servers) {
		pool.setServers(servers);
	}

	public void setWeights(Integer[] weights) {
		pool.setWeights(weights);
	}

	public void setInitConn(int initConn) {
		pool.setInitConn(initConn);
	}

	public void setMinConn(int minConn) {
		pool.setMinConn(minConn);
	}

	public void setMaxConn(int maxConn) {
		pool.setMaxConn(maxConn);
	}

	public void setMaxIdle(int maxIdel) {
		pool.setMaxIdle(maxIdel);
	}

	public void setMaintSleep(int maintSleep) {
		pool.setMaintSleep(maintSleep);
	}

	public void setNagle(boolean nagle) {
		pool.setNagle(nagle);

	}

	public void initialize() {
		pool.initialize();
		//System.out.println("DaoMemCacheClient init");
	}

	// set the read timeout to 3 secs
	public void setSocketTO(int socketTO) {
		pool.setSocketTO(socketTO);

	}

	// set a connect timeout
	public void setSocketConnectTO(int connectTO) {
		pool.setSocketConnectTO(connectTO);
	}

	// //////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////

	private static String key(Dao dao) throws NullKeyException, DaoBuildException {
		return dao.getClass() + dao.keySQL();
	}

	public void clear() {
		// DONOTHING
	}

	public boolean load(Dao dao) {
		String key = null;
		Object o=null;
		try{
		key=key(dao);
		o= mcc.get(key);
		if (o != null)
			dao.fetchFromObjectMap((HashMap)o);
		 //   log("Get", dao.getClass(), key, o!=null);
		}catch(Exception e){
			//load 不到，缓存里没有
		}
		return o != null;
	}

	public long getGetCount() {
		int c = 0;
		for(Object servStat : mcc.stats().values()) {
			long h = Long.valueOf((String)((Map)servStat).get("cmd_get"));
			c += h;
		}
		return c;
	}

	public long getHitCount() {
		int c = 0;
		for(Object servStat : mcc.stats().values()) {
			long h = Long.valueOf((String)((Map)servStat).get("get_hits"));
			c += h;
		}
		return c;
	}

	public float getHitRate() {
		return (float) getHitCount() / getGetCount();
	}

	public boolean put(Dao dao){
		String key = null;
		try{
		   key=key(dao);
		}catch(Exception e){
			e.printStackTrace();
		}
		boolean succ = mcc.set(key, dao.toMap());
	//	log("Put", dao.getClass(), key, succ);
		
		return succ;
	}

	private void log(String act, Class<? extends Dao> clazz, String key,boolean isHit) {
		Logger.debug("DaoCache " + act + " [" + isHit + "] " + clazz.getName()
				+ " key=[" + key + "]");
	}

	public void remove(Dao dao){
		try{
		mcc.delete(key(dao));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void remove(Dao[] daos) {
		for(int i=0;i<daos.length;i++){
			remove(daos[i]);
		}
	}
	

	public void remove(List<? extends Dao> daosList) {
		for(Dao dao:daosList){
			remove(dao);
		}
	}

	public void rset() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return mcc.stats().toString();
	}

}
