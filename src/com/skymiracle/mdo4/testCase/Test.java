package com.skymiracle.mdo4.testCase;

public class Test {

	public static void main(String[] args) {
		try {
			User.class.asSubclass(Test.class);
		System.out.println(true);
		}catch(ClassCastException e) {
			System.out.println("false");
		}
	}
}
