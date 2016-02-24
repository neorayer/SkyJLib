package com.skymiracle.util;

import java.text.NumberFormat;

public class NumberUtil {

	/**
	 * a 除以 b , 取digit小数点
	 * 
	 * @return
	 */
	public static double division(int a, int b, int digit) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(digit);
		return Double.parseDouble(nf.format(a * 1.0 / b));
	}

	/**
	 * a 除以 b , 取digit小数点
	 * 
	 * @return
	 */
	public static double division(long a, long b, int digit) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(digit);
		return Double.parseDouble(nf.format(a * 1.0 / b));
	}
}
