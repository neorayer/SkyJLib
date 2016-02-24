package com.skymiracle.maps.google;

import java.util.HashMap;
import java.util.Map;

public class AddrPoint {

	public static Map<String, String> accuracys = new HashMap<String, String>();
	{
		accuracys.put("0", "未知的地点");
		accuracys.put("1", "国家级准确度");
		accuracys.put("2", "地区（省/自治区/直辖市、辖区等）级准确度");
		accuracys.put("3", "子地区（县、市等）级准确度");
		accuracys.put("4", "城镇（城市、村镇）级准确度");
		accuracys.put("5", "邮政编码级准确度");
		accuracys.put("6", "街道级准确度");
		accuracys.put("7", "十字路口级准确度");
		accuracys.put("8", "地址级准确度");
		accuracys.put("9", "建筑物（楼房名称、物业名称、购物中心等）级准确度");
	}

	// 名称
	private String name;

	// 经度
	private String longitude;

	// 纬度
	private String latitude;

	private String accuracy;

	public AddrPoint(String name, String longitude, String latitude,
			String accuracy) {
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.accuracy = accuracy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public String getAccuracyValue() {
		return accuracys.get(accuracy);
	}

}
