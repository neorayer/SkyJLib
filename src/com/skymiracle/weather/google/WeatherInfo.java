package com.skymiracle.weather.google;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.mdo5.Mdo;

public class WeatherInfo extends Mdo<WeatherInfo>{
	private String cityName;
	
	public WeatherCurrent current = new WeatherCurrent();

	public List<WeatherForecast> forcasts = new LinkedList<WeatherForecast>();

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public WeatherCurrent getCurrent() {
		return current;
	}

	public void setCurrent(WeatherCurrent current) {
		this.current = current;
	}

	public List<WeatherForecast> getForcasts() {
		return forcasts;
	}

	public void setForcasts(List<WeatherForecast> forcasts) {
		this.forcasts = forcasts;
	}

	@Override
	public String[] keyNames() {
		return new String[]{"cityName"};
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}


}
