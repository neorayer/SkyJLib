package com.skymiracle.weather.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;

public class GoogleWeather {

	private String weatherUrl = "http://www.google.com/ig";

	private Map<String, CityCode> cityCodesMap = new HashMap<String, CityCode>();

	private static GoogleWeather instance = null;

	private Map<String, WeatherInfo> cachedWeatherInfoMap = new HashMap<String, WeatherInfo>();

	private long timeout = 60000; // MillSeconds

	private long lastTime = 0;

	public static GoogleWeather getInstance() {
		if (instance == null)
			instance = new GoogleWeather();
		return instance;
	}


	public GoogleWeather() {
		loadCityCodes();
	}
	public Map<String, CityCode> getCityCodesMap() {
		return cityCodesMap;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	private String elData(Element el, String elName) {
		return el.element(elName).attributeValue("data");
	}

	private void loadCityCodes() {
		Logger.debug("GoogleWeather load cities");

		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				"com/skymiracle/weather/google/cities.xml");
		try {
			SAXReader reader = new SAXReader();
			Document document = null;
			document = reader.read(is);

			Element rootElement = (Element) document
					.selectSingleNode("//xml_api_reply/cities");
			List<Element> cityEls = rootElement.elements("city");
			for (Element cityEl : cityEls) {
				String name = elData(cityEl, "name");
				String latitude_e6 = cityEl.element("latitude_e6")
						.attributeValue("data");
				String longitude_e6 = cityEl.element("longitude_e6")
						.attributeValue("data");

				CityCode cc = new CityCode(name, latitude_e6, longitude_e6);
				cityCodesMap.put(name, cc);
			}
		} catch (DocumentException e) {
			Logger.error("", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Logger.error("", e);
			}
		}

	}

	private String getWeatherXmlStr(String countryCode, String cityName,
			String charset) throws Exception {
		CityCode cc = cityCodesMap.get(cityName);
		if (cc == null)
			throw new Exception("No Such cityName=" + cityName);

		String laloStr = ",,," + cc.getLatitude_e6() + ','
				+ cc.getLongitude_e6();
		String urlStr = this.weatherUrl + "/api?hl=" + countryCode
				+ "&weather=" + laloStr;
		Logger.debug("GoogleWeather request " + urlStr);
		return StreamPipe.urlToString(urlStr, charset);
	}

	// private String getWeatherXmlStrOfChina(String cityName, String charset)
	// throws Exception {
	// return getWeatherXmlStr("zh-cn", cityName, charset);
	// }
	//
	// private String getWeatherXmlStrOfChina(String cityName) throws Exception
	// {
	// return getWeatherXmlStr("zh-cn", cityName, "GB2312");
	// }

	public WeatherInfo getWeatherInfo(String countryCode, String cityName,
			String charset) {
		String key = getKeyOfWeather(countryCode, cityName, charset);
		WeatherInfo wi = cachedWeatherInfoMap.get(key);
		if (wi == null) {
			return updateWeatherInfo(countryCode, cityName, charset);
		}

		if (System.currentTimeMillis() - this.lastTime > timeout) {
			return updateWeatherInfo(countryCode, cityName, charset);
		}
		return wi;
	}

	public WeatherInfo updateWeatherInfo(String countryCode, String cityName,
			String charset) {

		try {
			String xmlStr = getWeatherXmlStr(countryCode, cityName, charset);
			xmlStr = xmlStr.replace("&nbsp;", "");
			System.out.println(xmlStr);

			SAXReader reader = new SAXReader();
			reader.setEncoding(charset);
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlStr
					.getBytes(charset));
			Document document = reader.read(bais);
			bais.close();
			if (document == null)
				throw new Exception("Get weather xml format error!");

			WeatherInfo wi = new WeatherInfo();

			{
				Element rootEl = (Element) document
						.selectSingleNode("//xml_api_reply/weather/current_conditions");
				WeatherCurrent current = new WeatherCurrent();
				current.setCityName(cityName);
				current.setCondition(elData(rootEl, "condition"));
				current.setTemp_c(elData(rootEl, "temp_c"));
				current.setTemp_f(elData(rootEl, "temp_f"));
				current.setHumidity(elData(rootEl, "humidity"));
				current.setIcon(elData(rootEl, "icon"));
				current.setWind_condition(elData(rootEl, "wind_condition"));
				current.setIconUrl(getIconUrl(current));
				wi.current = current;

			}

			{
				Element rootEl = (Element) document
						.selectSingleNode("//xml_api_reply/weather");
				List<Element> els = rootEl.elements("forecast_conditions");
				for (Element el : els) {
					WeatherForecast forecast = new WeatherForecast();
					forecast.setCityName(cityName);
					forecast.setCondition(elData(el, "condition"));
					forecast.setDay_of_week(elData(el, "day_of_week"));
					forecast.setHigh(elData(el, "high"));
					forecast.setLow(elData(el, "low"));
					forecast.setIcon(elData(el, "icon"));
					forecast.setIconUrl(getIconUrl(forecast));
					wi.forcasts.add(forecast);
				}
			}
			this.lastTime = System.currentTimeMillis();
			String key = getKeyOfWeather(countryCode, cityName, charset);
			cachedWeatherInfoMap.put(key, wi);
			return wi;
		} catch (Exception e) {
			Logger.error("", e);
			return new WeatherInfo();
		}
	}

	private String getKeyOfWeather(String countryCode, String cityName,
			String charset) {
		return countryCode + "." + cityName + "." + charset;
	}

	public WeatherInfo getWeatherInfoOfChina(String cityName, String charset) {
		return getWeatherInfo("zh-cn", cityName, charset);
	}

	public WeatherInfo getWeatherInfoOfChina(String cityName) {
		return getWeatherInfoOfChina(cityName, "GB2312");
	}

	public String getIconUrl(WeatherCurrent current) {
//		return this.weatherUrl + current.getIcon();
		return "http://www.google.com" + current.getIcon();
	}

	public String getIconUrl(WeatherForecast forecast) {
//		return this.weatherUrl + forecast.getIcon();
		return "http://www.google.com" + forecast.getIcon();
	}

}
