package com.skymiracle.util.simpleMap;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCachedMap<K,V> implements SimpleMap<K,V>{
	private SockIOPool pool = SockIOPool.getInstance();
	
	private MemCachedClient mcc = new MemCachedClient();

	private boolean isInited = false;
	
	public MemCachedMap() {
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
		isInited = true;
	}

	// set the read timeout to 3 secs
	public void setSocketTO(int socketTO) {
		pool.setSocketTO(socketTO);
	}

	// set a connect timeout
	public void setSocketConnectTO(int connectTO) {
		pool.setSocketConnectTO(connectTO);
	}
	
	public V get(K k) {
		if (!isInited)
			initialize();
		return (V)mcc.get((String)k);
	}

	public void put(K k, V v) {
		if (!isInited)
			initialize();
		boolean res = mcc.set((String)k, v);
	}

	public void remove(K k) {
		if (!isInited)
			initialize();
		mcc.delete((String)k);
	}

}
