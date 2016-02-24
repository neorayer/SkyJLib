package com.skymiracle.test;

import com.skymiracle.logger.Logger;

public class TestHashCode {

	private int a = 230234;

	public void setA(int a) {
		this.a = a;
	}

	public int getA() {
		return this.a;
	}

	public static void main(String[] args) {
		System.out.println(new Object().hashCode());
		System.out.println(new TestHashCode().hashCode());
		System.out.println(new TestHashCode().hashCode());
		TestHashCode[] tchs = new TestHashCode[2000000];
		Logger.info("begin");
		for (int i = 0; i < tchs.length; i++)
			tchs[i] = new TestHashCode();
		Logger.info("step 1");
		for (int i = 0; i < tchs.length; i++) {
			if (tchs[i].getA() == 3)
				tchs[i].setA(4);

		}

		Logger.info("end");
	}
}
