package com.skymiracle.util;

/**
 * @author fatzhen
 * @create_date 2006-9-18
 */
public class IdCard15To18 {

	private static int[] weight = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
			8, 4, 2 };

	private static String[] result = { "1", "0", "X", "9", "8", "7", "6", "5",
			"4", "3", "2" };

	public static String getIdcard18(String idCard15) {
		if (idCard15.length() != 15)
			return "";
		StringBuffer s = new StringBuffer(18);
		s.append(idCard15.substring(0, 6));
		s.append("19");
		s.append(idCard15.substring(6));
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			int a = s.charAt(i) - 48;
			sum += (a * weight[i]);
		}
		int x = sum % 11;
		return s + result[x];
	}

	public static void main(String[] args) throws Exception {
		String s = "340524800101001";
		System.out.println(getIdcard18(s));
	}
}
