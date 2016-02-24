package com.skymiracle.fileBox;

import java.util.HashMap;

public class FileBoxSizeFileLockFactory {

	public static HashMap<String, Object> hm = new HashMap<String, Object>();

	public static Object getSizeFileLock(String key) {
		Object o = hm.get(key);
		if (o != null)
			return o;
		else {
			o = new Object();
			hm.put(key, o);
			return o;
		}
	}

}
