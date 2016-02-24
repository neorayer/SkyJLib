package com.skymiracle.csv;

import java.util.Comparator;

public class MyComparator implements Comparator<String[]> {
	private String orderLabel;

	private String orderLabelType;

	private boolean ascend;

	private String[] labels = null;

	public MyComparator(String orderLabel, String orderLabelType,
			boolean ascend, String[] labels) {
		this.orderLabel = orderLabel;
		this.orderLabelType = orderLabelType;
		this.ascend = ascend;
		this.labels = labels;
	}

	public int compare(String[]valueLine1, String[] valueLine2) {
		int index = 0;
		for (int i = 0; i < this.labels.length; i++)
			if (this.labels[i].trim().equalsIgnoreCase(this.orderLabel)) {
				index = i;
				break;
			}
		if (this.orderLabelType.equals("int")) {
			if (this.ascend)
				return this.isMax(Integer.parseInt(valueLine1[index]), Integer
						.parseInt(valueLine2[index]));
			else
				return this.isMax(Integer.parseInt(valueLine2[index]), Integer
						.parseInt(valueLine1[index]));
		} else if (this.orderLabelType.equals("double")) {
			if (this.ascend)
				return this.isMax(Double.parseDouble(valueLine1[index]), Double
						.parseDouble(valueLine2[index]));
			else
				return this.isMax(Double.parseDouble(valueLine2[index]), Double
						.parseDouble(valueLine1[index]));
		} else {
			if (this.ascend)
				return this.isMax(valueLine1[index], valueLine2[index]);
			else
				return this.isMax(valueLine2[index], valueLine1[index]);
		}
	}

	private int isMax(String arg0, String arg1) {
		int arg0Len = arg0.length();
		int arg1Len = arg1.length();
		int comIndex = arg0Len >= arg1Len ? arg1Len : arg0Len;

		for (int i = 0; i < comIndex; i++) {
			if (arg0.charAt(i) > arg1.charAt(i))
				return 1;
			else if (arg0.charAt(i) < arg1.charAt(i))
				return 0;
			else {
			}
		}
		if (arg0Len == arg1Len)
			return 0;
		else if (arg0Len > arg1Len)
			return 1;
		else
			return 0;
	}

	private int isMax(int arg0, int arg1) {
		if (arg0 >= arg1)
			return 1;
		else
			return 0;
	}

	private int isMax(double arg0, double arg1) {
		if (arg0 >= arg1)
			return 1;
		else
			return 0;
	}
}
