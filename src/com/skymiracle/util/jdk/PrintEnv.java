package com.skymiracle.util.jdk;

import java.util.Map;
import java.util.Properties;

public class PrintEnv {

	public static void main(String[] args) {
		System.out.println("Env");
		for(Map.Entry<String, String> e:System.getenv().entrySet()) {
			System.out.println(e.getKey() + "=" + e.getValue());
		}
		System.out.println();
		System.out.println("Props");
		for(Map.Entry<Object, Object> e:System.getProperties().entrySet()) {
			System.out.println(e.getKey() + "=" + e.getValue());
		}
	}
}
