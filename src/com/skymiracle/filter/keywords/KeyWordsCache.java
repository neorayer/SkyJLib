package com.skymiracle.filter.keywords;

public class KeyWordsCache {
	private static KeyWordsCache cache = new KeyWordsCache();
	private boolean isOpen;

	private KeyWordsCache() {
	};

	public static KeyWordsCache getInstance() {
		return cache;
	}

	private KeyWord[] keyWords = new KeyWord[0];
	private String filterClassName;

	public KeyWord[] getKeyWords() {
		return this.keyWords;
	}

	public void setKeyWords(KeyWord[] keyWords) {
		this.keyWords = keyWords;
	}

	public void runFetcher(Fetcher ftecher) {
		ftecher.setCache(this);
		new Thread(ftecher).start();
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void setFilterClassName(String filterClassName) {
		this.filterClassName = filterClassName;

	}

	public String getFilterClassName() {
		return this.filterClassName;
	}
}
