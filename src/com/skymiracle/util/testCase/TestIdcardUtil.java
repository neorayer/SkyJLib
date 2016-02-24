package com.skymiracle.util.testCase;

import com.skymiracle.util.IdcardUtil;

import junit.framework.TestCase;

public class TestIdcardUtil extends TestCase {
	
	private String id15;
	
	private String id18;
	
	@Override
	protected void setUp() throws Exception {
		id15 = "352127770313001";
		id18 = "352127197703130012";
	}

	public void testChange15To18() {
		assertEquals("15位转18位失败!", id18, IdcardUtil.changeTo18(id15));
	}
	
	public void testChange18To15() {
		assertEquals("18位转15位失败!", id15, IdcardUtil.changeTo15(id18));
	}
}
