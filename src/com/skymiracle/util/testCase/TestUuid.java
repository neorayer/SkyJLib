package com.skymiracle.util.testCase;

import java.util.*;

public class TestUuid {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int c = 0; c < 1000; c++) {
			Map map = new HashMap();
			for (int i = 0; i < 10000; i++) {
				String uuid = UUID.randomUUID().toString();
				map.put(uuid, uuid);
			}
			if (map.size() != 10000)
				System.out.println(map.size());
		}
	}

}
