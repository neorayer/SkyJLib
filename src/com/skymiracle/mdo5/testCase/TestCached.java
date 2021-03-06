package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.*;

import com.skymiracle.mdo5.testCase.Mdo5User.STATUS;
import com.skymiracle.sor.exception.AppException;

import junit.framework.TestCase;

public class TestCached extends TestCase {

	static {
		//Logger.setLevel(Logger.LEVEL_DETAIL);
	}

	@Override
	public void setUp() throws AppException, Exception {
		store.createTableForce(Mdo5User.class, true);
		int COUNT = 1;
		for (int i = 0; i < COUNT; i++) {
			Mdo5User user = new Mdo5User();
			user.setUid("tom" + i);
			user.setDc("skymiracle.com");
			user.setIsPop3(true);
			user.setStatus(STATUS.open);
			user.setResume(new StringBuffer("李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。"
					+ "   1970年12月至1973年3月，解放军1619部队战士。"
					+ " 1973年3月至1976年5月，武汉市国棉二厂工人。"
					+ "1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。"
					+ "1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"
					+ "李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。"
					+ "   1970年12月至1973年3月，解放军1619部队战士。"
					+ " 1973年3月至1976年5月，武汉市国棉二厂工人。"
					+ "1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。"
					+ "1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"
					+ "李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。"
					+ "   1970年12月至1973年3月，解放军1619部队战士。"
					+ " 1973年3月至1976年5月，武汉市国棉二厂工人。"
					+ "1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。"
					+ "1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"
					+ "李宪生，男，1954年9月生，山西阳曲人。在职博士研究生。"
					+ "   1970年12月至1973年3月，解放军1619部队战士。"
					+ " 1973年3月至1976年5月，武汉市国棉二厂工人。"
					+ "1976年5月至1977年1月，湖北省武汉市纺织局党办秘书。"
					+ "1977年1月至1980年1月，武汉师范学院政治系政治教育专业学习。"));
			user.create();
			System.out.println(i);
		}
	}


	public void testFoo() throws Exception {
		long begin = System.currentTimeMillis();
		int COUNT = 110;
		for (int c = 0; c < COUNT; c++)
			for (int i = 0; i < 1; i++) {
				Mdo5User user = new Mdo5User();
				user.setUid("tom" + i);
				user.setDc("skymiracle.com");
				user.load();
			}
		long spent = System.currentTimeMillis() - begin;
		System.out
				.println(((float) COUNT * 1000 / spent) + " sqls per seconds");

		if (store.getMdoCache() != null)
		System.out.println(store.getMdoCache().getHitRate());
	}

	public static void main(String[] args) throws Exception {

	}

}
