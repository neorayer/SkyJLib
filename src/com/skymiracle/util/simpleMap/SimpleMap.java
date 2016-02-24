package com.skymiracle.util.simpleMap;


public interface SimpleMap<K,V>{

	public void put(K k, V v);

	public V get(K k);
	
	public void remove(K k);
	
}
