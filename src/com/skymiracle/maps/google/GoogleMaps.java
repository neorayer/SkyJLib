package com.skymiracle.maps.google;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.skymiracle.logger.*;

import com.skymiracle.io.StreamPipe;

public class GoogleMaps {
	private String geoUrl = "http://maps.google.com/maps/geo";

	private String mapsKey;

	private String output = "csv";

	private String geoUrlTemplate = geoUrl + "?q=%s&key=" + mapsKey
			+ "&output=" + output + "&gl=cn&sensor=false";

	private static GoogleMaps instance = null;

	public Map<String, String> statusCodes = new HashMap<String, String>();
	{
		statusCodes.put("200", "未出现错误，已对地址成功地进行了解析，并返回其地址解析");
		statusCodes.put("400",
				"无法成功解析行车路线请求。例如，如果此请求包含的路标数大于允许的最大最大数，则该请求可能已被拒绝");
		statusCodes.put("500", "无法成功处理地址解析或行车路线请求，但是确切的失败原因未知");
		statusCodes
				.put("601",
						"HTTP q 参数缺失或没有值。对于地址解析请求，这意味着将空地址指定为输入。对于行车路线请求，这意味着在输入中未指定查询");
		statusCodes.put("602", "找不到指定地址的对应地理位置。这可能是地址比较新，或地址不正确");
		statusCodes.put("603", "由于合法性或合同原因，无法返回指定地址的地址解析或指定行车路线查询的路线");
		statusCodes
				.put("604",
						"Gdirections 对象无法计算查询中提到的两点之间的行车路线。这通常是因为两点之间无可用路线，或我们没有该地区的路线数据。");
		statusCodes.put("610", "指定的密钥无效或与指定的域不匹配");
		statusCodes
				.put(
						"620",
						"指定的密钥超出了 24 小时的请求限制或在过短的一段时间内提交了过多的请求。如果您要同时或循环发送多个请求，请在代码中使用计时器或暂停以确保不会过快地发送请求");
	}

	public static GoogleMaps getInstance(String mapsKey) {
		if (instance == null)
			instance = new GoogleMaps(mapsKey);
		else
			instance.setMapsKey(mapsKey);
		
		return instance;
	}

	private GoogleMaps(String mapsKey) {
		this.mapsKey = mapsKey;
	}
	
	public AddrPoint getAddrPoint(String addrname) {
		try {
			String pointStr = getAddrPointStr(addrname);
			Logger.debug(pointStr);
			String[] ss = pointStr.split(",");

			if (ss.length != 4)
				return null;

			String statusCode = ss[0];
			String accuracy = ss[1];
			String longitude = ss[2];
			String latitude = ss[3];

			if (!"200".equals(statusCode))
				throw new Exception(statusCodes.get(statusCode));

			return new AddrPoint(addrname, longitude, latitude, accuracy);
		} catch (Exception e) {
			Logger.error("", e);
			return null;
		}
	}
 
	private String getAddrPointStr(String addrname) throws IOException {
		String urlStr = String.format(geoUrlTemplate, URLEncoder.encode(
				addrname, "utf-8"));
		return StreamPipe.urlToString(urlStr, "utf-8");
	}

	public String getMapsKey() {
		return mapsKey;
	}

	public void setMapsKey(String mapsKey) {
		this.mapsKey = mapsKey;
	}

	public static void main(String[] args) {
		Logger.setLevel(Logger.LEVEL_DEBUG);
		GoogleMaps maps = GoogleMaps.getInstance("ABQIAAAAA0r1EbwAk9UvZlwwN8g_nxQQGJX5NBbd6cbTehW9UtTdTTtayxRuFGcD2Hx7VPkil_VOLMJE9lO4tw");
		AddrPoint point = maps.getAddrPoint("上海 虹口 虹口 溧阳路57弄107号");
		System.out.println(point.getAccuracyValue());
	}
}
