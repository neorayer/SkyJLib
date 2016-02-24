package com.skymiracle.util;

public class IdcardUtil {
	public static String changeTo18(String idcard) {
		String id18 = "";

		if (idcard.length() == 18) {
			id18 = idcard.toUpperCase();
		} else if (idcard.length() == 15) {
			int[] w = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
			char[] A = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
			String id17 = idcard.substring(0, 6) + "19" + idcard.substring(6, 15);
			int[] id17Array;
			id17Array = new int[17];
			for (int i = 0; i < 17; i++) {
				id17Array[i] = Integer.parseInt(id17.substring(i, i + 1));
			}
			int s = 0;
			for (int i = 0; i < 17; i++) {
				s = s + id17Array[i] * w[i];
			}
			s = s % 11;
			id18 = id17 + Character.toString(A[s]);
		}

		return id18;
	}

	public static String changeTo15(String idcard) {
		String id15 = "";
		if (idcard.length() == 15) {
			id15 = idcard.toUpperCase();
		} else if (idcard.length() == 18) {
			id15 = idcard.substring(0, 6) + idcard.substring(8, 17);
		}

		return id15;
	}
}
