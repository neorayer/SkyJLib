package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.Mdo5UserX;
import static com.skymiracle.mdo5.testCase.Singletons.store;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.MList;
import com.skymiracle.mdo5.testCase.Mdo5User.STATUS;
import com.skymiracle.sor.exception.AppException;

import junit.framework.TestCase;

public class TestMdoMap extends TestCase {
	
	static {
		Logger.setLevel(Logger.LEVEL_DETAIL);
	}

	@Override
	public void setUp() throws AppException, Exception {
		store.createTableForce(Mdo5User.class, true);
		
		for (int i = 0; i < 4; i++) {
			Mdo5User user = new Mdo5User();
			user.setUid("tom" + i);
			user.setDc("skymiracle.com");
			user.setIsPop3(true);
			user.setStatus(STATUS.open);
			user.create();
		}
	}
	
	public void testMdoMap() throws AppException, Exception {
		// has limitBegin and limitCount
		MList<Mdo5User> list1 = Mdo5UserX.find("dc:2,1", "skymiracle.com"); 
		assertEquals(1,list1.size());
		
		// has limitCount but not limitBegin
		MList<Mdo5User> list2 = Mdo5UserX.find("dc-:3", "skymiracle.com"); 
		assertEquals(3,list2.size());
		
		// not need page
		MList<Mdo5User> list3 = Mdo5UserX.find("dc", "skymiracle.com"); 
		assertEquals(4,list3.size());
		
		// order by uid desc
		MList<Mdo5User> list4 = Mdo5UserX.find("uid-, dc:2", null, "skymiracle.com");
		assertEquals(2,list4.size());
		assertEquals("tom3",list4.get(0).uid);
		assertEquals("tom2",list4.get(1).uid);
		
		MList<Mdo5User> list5 = Mdo5UserX.find("uid+");
		assertEquals(4,list5.size());
		assertEquals("tom0",list5.get(0).uid);
	}

	
}
