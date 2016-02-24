package com.skymiracle.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TestUtil {

	public static void printMap(HashMap hm) {
		Set keys = hm.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key + " : " + hm.get(key));
		}
	}
}
