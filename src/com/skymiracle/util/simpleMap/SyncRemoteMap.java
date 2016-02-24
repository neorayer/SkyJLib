package com.skymiracle.util.simpleMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程同步Map。对象同时存放在local的hashmap和远程的Memcache里。
 * 写入修改时，local和memcache同时写入。
 * 读取时，先读取local,如果不存在则从memcache读取。
 * 
 * @author zhourui
 *
 * @param <K>
 * @param <V>
 */
public  class SyncRemoteMap<K, V> implements SimpleMap<K, V> {

	private Map<K, V> localMap = new ConcurrentHashMap<K, V>();

	private SimpleMap<K, V> remoteMap;

	public SimpleMap<K, V> getRemoteMap() {
		return remoteMap;
	}

	public void setRemoteMap(SimpleMap<K, V> remoteMap) {
		this.remoteMap = remoteMap;
	}

	public V get(K k) {
		V v = localMap.get(k);
		if (v == null) {
			v = remoteMap.get(k);
			if (v == null)
				return null;
			localMap.put(k, v);
			return v;
		}
		return v;
	}

	public void put(K k, V v) {
		localMap.put(k, v);
		remoteMap.put(k, v);
	}

	public void remove(K k) {
		remoteMap.remove(k);
		localMap.remove(k);
	}

	public void clearLocal() {
		localMap.clear();
	}
}
