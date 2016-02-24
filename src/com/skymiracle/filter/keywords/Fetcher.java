package com.skymiracle.filter.keywords;

public abstract class Fetcher implements Runnable {
	protected KeyWordsCache cache;

	public void setCache(KeyWordsCache cache) {
		this.cache = cache;
	}

	public abstract void run();

}
