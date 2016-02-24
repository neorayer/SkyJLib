package com.skymiracle.mdo5.cache;

import java.util.Map;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.skymiracle.mdo5.MList;
import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.MdoBuildException;
import com.skymiracle.mdo5.MdoMap;
import com.skymiracle.mdo5.NullKeyException;

public class MdoCacheMemcached implements MdoCache {

	private SockIOPool pool = SockIOPool.getInstance();
	
	private MemCachedClient mcc = new MemCachedClient();

	public MdoCacheMemcached() {
		pool.setServers(new String[] { "localhost:11211" });
		pool.setWeights(new Integer[] { 3, 3, 2 });
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
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

	private static String key(Mdo<?> mdo) throws NullKeyException,
			MdoBuildException {
		return mdo.getClass() + mdo.keySQL();
	}

	public void clear() {
		// DONOTHING
	}

	public boolean load(Mdo<?> mdo) {
		String key = key(mdo);
		Object o = mcc.get(key);
		if (o != null)
			mdo.filledBy((MdoMap)o);
//		log("Get", mdo.mdoClass(), key, o != null);
		return o != null;
	}
	
	public boolean exists(Mdo<?> mdo) {
		String key = key(mdo);
		Object o = mcc.get(key);
		return o != null;
	}

	@SuppressWarnings("unchecked")
	public long getGetCount() {
		int c = 0;
		for(Object servStat : mcc.stats().values()) {
			long h = Long.valueOf((String)((Map)servStat).get("cmd_get"));
			c += h;
		}
		return c;
	}

	@SuppressWarnings("unchecked")
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

	public boolean put(Mdo<?> mdo) {
		String key = key(mdo);
		boolean succ = mcc.set(key, mdo.toMdoMap());
//		log("Put", mdo.mdoClass(), key, succ);
		return succ;
	}

//	private void log(String act, Class<? extends Mdo<?>> clazz, String key,
//			boolean isHit) {
//		Logger.detail("MdoCache " + act + " [" + isHit + "] " + clazz.getName()
//				+ " key=[" + key + "]");
//	}

	public void remove(Mdo<?> mdo) {
		mcc.delete(key(mdo));
	}

	public void remove(MList<?> mdos) {
		for (Mdo<?> mdo : mdos)
			remove(mdo);
	}

	public void rset() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return mcc.stats().toString();
	}
}
