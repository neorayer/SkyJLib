package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.*;

import java.util.UUID;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.MList;
import com.skymiracle.mdo5.testCase.Mdo5User.STATUS;
import com.skymiracle.sor.exception.AppException;

import junit.framework.TestCase;

public class TestFullText extends TestCase {

	static {
		Logger.setLevel(Logger.LEVEL_DETAIL);
	}

	@Override
	public void setUp() throws AppException, Exception {
		store.createTableForce(Mdo5User.class, true);
		Mdo5UserX.rebuildFullTextSearcherIndex();
	}

	public void testFoo() throws Exception {
		long begin = System.currentTimeMillis();
		int COUNT = 4;
		for (int i = 0; i < COUNT; i++) {
			Mdo5User user = new Mdo5User();
			user.setUid("tom" + i + UUID.randomUUID().toString());
			user.setDc("skymiracle.com");
			user.setIsPop3(true);
			user.setStatus(STATUS.open);
			user.setResume(new StringBuffer("eclipse, eclipse, eclipse, eclipse, eclipse, eclipse, eclipse" ));
//			user.setResume(new StringBuffer(
//					"李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。" +
//					"   1970年12月至1973年3月，解放军1619部队战士。" +
//					" 1973年3月至1976年5月，武汉市国棉二厂工人。" +
//					"1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。" +
//					"1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"+
//					"李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。" +
//					"   1970年12月至1973年3月，解放军1619部队战士。" +
//					" 1973年3月至1976年5月，武汉市国棉二厂工人。" +
//					"1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。" +
//					"1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"+
//					"李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。" +
//					"   1970年12月至1973年3月，解放军1619部队战士。" +
//					" 1973年3月至1976年5月，武汉市国棉二厂工人。" +
//					"1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。" +
//					"1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"+
//					"李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。" +
//					"   1970年12月至1973年3月，解放军1619部队战士。" +
//					" 1973年3月至1976年5月，武汉市国棉二厂工人。" +
//					"1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。" +
//					"1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"
//					));
			user.create();
		}
		long spent = System.currentTimeMillis() - begin;
		System.out.println(((float)COUNT * 1000 / spent ) + " sqls per seconds");
		//		assertEquals(COUNT, Mdo5UserX.count());
		
		{
			Mdo5User user = new Mdo5User();
			user.setUid("tom1");
			user.setDc("skymiracle.com");
			user.delete();
		}
		//		assertEquals(COUNT - 1, Mdo5UserX.count());
		
		{
			Mdo5User user = new Mdo5User();
			user.setUid("tom2");
			user.setDc("skymiracle.com");
			user.update("dc,status", "ko10000.com", Mdo5User.STATUS.delete);
		}
		//		assertEquals(COUNT - 2, Mdo5UserX.count("dc", "skymiracle.com"));
		

		begin = System.currentTimeMillis();
		MList<Mdo5User> users = Mdo5UserX.search(1, 2000, "eclipse", "resume");
		System.out.println("Search spent: " + (System.currentTimeMillis() - begin));
		System.out.println("Search hits:" + users.size());
		
		
	}

    public static void main(String[] args) throws  Exception {
        TestFullText t = new TestFullText();
        t.setUp();
        t.testFoo();
    }
	
}
