package com.skymiracle.util;

public class TimeLength {
	private long milliSecond;

	private int day;

	private int hour;

	private int minute;

	private int second;

	public TimeLength(long milliSecond) {
		this.milliSecond = milliSecond;
		parse();
	}

	/**
	 * Create a TimeLength object with float type second.
	 * 
	 * @param second
	 */
	public TimeLength(float second) {
		this((long) (second * 1000));
	}

	private void parse() {
		int s = (int) (this.milliSecond / 1000);
		int m = s / 60;
		int h = m / 60;
		int d = h / 24;

		this.day = d;
		this.hour = h - this.day * 24;
		this.minute = m - this.day * 24 * 60 - this.hour * 60;
		this.second = s - this.day * 24 * 60 * 60 - this.hour * 60 * 60
				- this.minute * 60;
	}

	public int getDay() {
		return this.day;
	}

	public int getHour() {
		return this.hour;
	}

	public int getMinute() {
		return this.minute;
	}

	public int getSecond() {
		return this.second;
	}
}
