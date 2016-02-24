package com.skymiracle.weather.google;

import com.skymiracle.mdo5.Mdo;

public class WeatherCurrent extends Mdo<WeatherCurrent>{
	private String cityName = "";

	private String condition= "";

	private String temp_f= "-10000";

	private String temp_c= "-10000";

	private String humidity = "";

	private String icon = "";

	private String wind_condition = "";

	private String iconUrl;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getTemp_f() {
		return temp_f;
	}

	public void setTemp_f(String temp_f) {
		this.temp_f = temp_f;
	}

	public String getTemp_c() {
		return temp_c;
	}

	public void setTemp_c(String temp_c) {
		this.temp_c = temp_c;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getWind_condition() {
		return wind_condition;
	}

	public void setWind_condition(String wind_condition) {
		this.wind_condition = wind_condition;
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
