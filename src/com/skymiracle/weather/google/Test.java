package com.skymiracle.weather.google;

import com.skymiracle.logger.Logger;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void testGoogleWeather() throws Exception {
		Logger.setLevel(Logger.LEVEL_DEBUG);
		for (int i = 0; i < 100; i++) {
			GoogleWeather googleWeather = GoogleWeather.getInstance();

			WeatherInfo wi = googleWeather.getWeatherInfoOfChina("金华");
			System.out.println(wi.toString("\r\n"));
		}
	}
}
