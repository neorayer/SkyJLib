package com.skymiracle.util;

public class ColorUtils {

	/**
	 * 
	 * @param webColor
	 *            etc. #fffff #c3d9ff
	 * @return
	 */
	public static int[] getRgbValue(String webColor) {
		if (webColor.startsWith("#"))
			webColor = webColor.substring(1);

		if (webColor.length() != 6)
			return new int[] { 0, 0, 0 };

		int[] res = new int[3];
		res[0] = Integer.parseInt(webColor.substring(0, 2), 16);
		res[1] = Integer.parseInt(webColor.substring(2, 4), 16);
		res[2] = Integer.parseInt(webColor.substring(4, 6), 16);
		return res;
	}

	public static void main(String[] args) {
		int[] res = ColorUtils.getRgbValue("#ffffff");
		for (int v : res) {
			System.out.println(v);
		}
	}
}
