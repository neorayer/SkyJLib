package com.skymiracle.auth.testCase;

import junit.framework.TestCase;

import com.skymiracle.auth.*;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UsernameWithDomain;

public class TestPopAuthImpl extends TestCase {

	public void testPopAuthImpl() {
		Logger.setLevel(Logger.LEVEL_DETAIL);
		
		Authable auth = new PopAuthImpl();
		{
			UsernameWithDomain uwd = auth.auth("demo", "skymiracle.com",
					"111111", "Test", "127.0.0.1");
			assertNotNull(uwd);
		}
		{
			UsernameWithDomain uwd = auth.auth("demo", "skymiracle.com",
					"errorPass", "Test", "127.0.0.1");
			assertNull(uwd);
		}

	}
}
