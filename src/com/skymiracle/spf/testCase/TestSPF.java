package com.skymiracle.spf.testCase;

import com.skymiracle.spf.SPFChecker;
import com.skymiracle.spf.SPFCheckerImpl;

import junit.framework.TestCase;

public class TestSPF extends TestCase { 

	private SPFChecker spfSchecker;

	public void testSPFChecker() {
		// TODO get the instance of SPFChecker
		spfSchecker = new SPFCheckerImpl(); // 

		// hotmail.com
		assertEquals(SPFChecker.RESULT_SUCC, spfSchecker.doCheck(
				"216.32.240.10", "hotmail.com"));
		assertEquals(SPFChecker.RESULT_FAIL, spfSchecker.doCheck(
				"220.181.13.9", "hotmail.com"));

		// sina.com
//		assertEquals(SPFChecker.RESULT_SUCC, spfSchecker.doCheck(
//				"202.108.43.230", "sina.com"));
		assertEquals(SPFChecker.RESULT_FAIL, spfSchecker.doCheck(
				"220.181.13.9", "sina.com"));

		// gmail.com
		assertEquals(SPFChecker.RESULT_SUCC, spfSchecker.doCheck(
				"209.85.128.0", "gmail.com"));
		assertEquals(SPFChecker.RESULT_FAIL, spfSchecker.doCheck(
				"220.181.13.9", "sina.com"));

		
		// 163.com
		assertEquals(SPFChecker.RESULT_SUCC, spfSchecker.doCheck(
				"220.181.12.9", "163.com"));
		assertEquals(SPFChecker.RESULT_FAIL, spfSchecker.doCheck(
				"220.18.13.9", "163.com"));

		assertEquals(SPFChecker.RESULT_NONE, spfSchecker.doCheck(
				"220.18.13.9", "skymiarcle.com"));
	
		// localhost
		assertEquals(SPFChecker.RESULT_FAIL, spfSchecker.doCheck(
				"127.0.0.1", "gmail.com"));

	
	}
}
