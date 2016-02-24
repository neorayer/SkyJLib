package com.skymiracle.mdo5.testCase;

import junit.framework.TestCase;

import com.skymiracle.mdo5.MList;
import com.skymiracle.sor.exception.AppException;

public class TestMList extends TestCase {

	MList<Mdo5User> list = new MList<Mdo5User>();

	@Override
	public void setUp() throws AppException, Exception {
		list = new MList<Mdo5User>();
		for (int i = 0; i < 10; i++) {
			Mdo5User user = new Mdo5User();
			user.setDc("user_" + i);
			list.add(user);
		}
	}

	public void testSub() {
		// count < size and from 0
		{
			int begin = 0;
			int count = 6;
			MList<Mdo5User> sub = list.sub(begin, count);
			assertEquals("取出的记录数不对", count, sub.size());
			assertEquals("第一个记录取的不对", "user_" + begin, sub.get(0).getDc());
			assertEquals("最后一个记录取的不对", "user_" + (begin + count - 1), sub.get(
					count - 1).getDc());
		}

		// count < size and from 3
		{
			int begin = 3;
			int count = 6;
			MList<Mdo5User> sub = list.sub(begin, count);
			assertEquals("取出的记录数不对", count, sub.size());
			assertEquals("第一个记录取的不对", "user_" + begin, sub.get(0).getDc());
			assertEquals("最后一个记录取的不对", "user_" + (begin + count - 1), sub.get(
					count - 1).getDc());
		}

		// count == size
		{
			int begin = 0;
			int count = 10;
			MList<Mdo5User> sub = list.sub(begin, count);
			assertEquals("取出的记录数不对", count, sub.size());
			assertEquals("第一个记录取的不对", "user_" + begin, sub.get(0).getDc());
			assertEquals("最后一个记录取的不对", "user_" + (begin + count - 1), sub.get(
					count - 1).getDc());
		}

		// count > size and from 0
		{
			int begin = 0;
			int count = 15;
			MList<Mdo5User> sub = list.sub(begin, count);
			assertEquals("取出的记录数不对", sub.size(), sub.size());
			assertEquals("第一个记录取的不对", "user_" + begin, sub.get(0).getDc());
			assertEquals("最后一个记录取的不对", "user_" + (begin + sub.size() - 1), sub
					.get(sub.size() - 1).getDc());
		}

		// count > size and from 3
		{
			int begin = 3;
			int count = 15;
			MList<Mdo5User> sub = list.sub(begin, count);
			assertEquals("取出的记录数不对", sub.size(), sub.size());
			assertEquals("第一个记录取的不对", "user_" + begin, sub.get(0).getDc());
			assertEquals("最后一个记录取的不对", "user_" + (begin + sub.size() - 1), sub
					.get(sub.size() - 1).getDc());
		}
	}
}
