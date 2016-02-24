package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.*;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.testCase.Mdo5User.STATUS;
import com.skymiracle.sor.exception.AppException;

import junit.framework.TestCase;

public class TestMdo5 extends TestCase {

	static {
		Logger.setLevel(Logger.LEVEL_DETAIL);
	}

	@Override
	public void setUp() throws AppException, Exception {
		store.createTableForce(Mdo5User.class, true);
	}

	public void testFoo() throws Exception {
		long begin = System.currentTimeMillis();
		int COUNT = 4;
		for (int i = 0; i < COUNT; i++) {
			Mdo5User user = new Mdo5User();
			user.setUid("tom" + i);
			user.setDc("skymiracle.com");
			user.setIsPop3(true);
			user.setStatus(STATUS.open);
			user.setResume(new StringBuffer("李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。"
					+ "   1970年12月至1973年3月，解放军1619部队战士。" + " 1973年3月至19。"));
			user.create();
		}

		{
			Mdo5User user = new Mdo5User();
			user.setUid("tom1");
			user.setDc("skymiracle.com");
			this.assertTrue(user.exists());
		}

		long spent = System.currentTimeMillis() - begin;
		System.out
				.println(((float) COUNT * 1000 / spent) + " sqls per seconds");
		assertEquals(COUNT, Mdo5UserX.count());

		{
			Mdo5User user = new Mdo5User();
			user.setUid("tom1");
			user.setDc("skymiracle.com");
			user.delete();
		}
		assertEquals(COUNT - 1, Mdo5UserX.count());

		{
			Mdo5User user = new Mdo5User();
			user.setUid("tom2");
			user.setDc("skymiracle.com");
			user.update("dc,status", "ko10000.com", Mdo5User.STATUS.delete);
		}
		assertEquals(COUNT - 2, Mdo5UserX.count("dc", "skymiracle.com"));

	}

}
