package com.skymiracle.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class CollectionUtil {

	public static void randomSortList(List list) {
		Random random = new Random();
		List<IntTagPair> itpList = new LinkedList<IntTagPair>();
		for (Object o : list) {
			IntTagPair itp = new IntTagPair();
			itp.tag = random.nextInt();
			itp.o = o;

			itpList.add(itp);
		}

		Collections.sort(itpList, new Comparator<IntTagPair>() {
			public int compare(IntTagPair o1, IntTagPair o2) {
				return o1.tag - o2.tag;
			}
		});

		list.clear();
		for (IntTagPair itp : itpList) {
			list.add(itp.o);
		}
	}
}
