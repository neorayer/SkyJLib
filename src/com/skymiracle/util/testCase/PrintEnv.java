package com.skymiracle.util.testCase;

import java.util.Map;

public class PrintEnv {

	public static void main(String... args) {
		Map<String, String> env = System.getenv();
		for(Map.Entry<String, String> e: env.entrySet()) {
			System.out.println(e.getKey() + " = " + e.getValue());
		}
	}
}
