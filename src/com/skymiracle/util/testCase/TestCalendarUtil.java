package com.skymiracle.util.testCase;

import java.text.ParseException;

import com.skymiracle.util.CalendarUtil;

import junit.framework.TestCase;

public class TestCalendarUtil extends TestCase {

	public void test() throws ParseException {
		String date = "2007-09-09";
		assertEquals(CalendarUtil.getDateFromDate(date, 1, "yyyy-MM-dd"),
				"2007-09-10");
		assertEquals(CalendarUtil.getDateFromDate(date, -1, "yyyy-MM-dd"),
				"2007-09-08");
	}
}
