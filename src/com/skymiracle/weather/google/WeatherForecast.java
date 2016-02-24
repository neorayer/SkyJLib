package com.skymiracle.weather.google;

import com.skymiracle.mdo5.Mdo;

public class WeatherForecast extends Mdo<WeatherForecast> {

	private String cityName = "";

	private String day_of_week = "";
	
	private String low = "-10000";

	private String high = "-10000";

	private String icon = "";

	private String condition = "";

	private String iconUrl;
	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "cityName" };
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

}
