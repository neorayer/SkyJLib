package com.skymiracle.auth.testCase;

import junit.framework.TestCase;

import com.skymiracle.auth.*;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UsernameWithDomain;

public class TestPopAuthForAntiCracker extends TestCase {

	public void testPopAuthImpl() {
		Logger.setLevel(Logger.LEVEL_DETAIL);
		
		PopAuthImpl auth = new PopAuthImpl();
		auth.setPop3Host("skymiracle.com");
		auth.setPop3Port(110);
		for (int i=0; i<190; i++){
			UsernameWithDomain uwd = auth.auth("zhuli1@skymiracle.com", "skymiracle.com",
					"111111", "Test", "skymiracle.com");
//			assertNull(uwd);
		}
	}
}
