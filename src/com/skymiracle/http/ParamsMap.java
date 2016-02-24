package com.skymiracle.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParamsMap extends HashMap<String, String> {

	private static final long serialVersionUID = -8374957021302544382L;
	private Map<String, List<String>> multiValuesMap =new HashMap<String, List<String>> ();
	public String[] getValues(String key) {
		List<String> values = multiValuesMap.get(key);
		return values == null ? new String[0] : values.toArray(new String[0]);
	}
	
	@Override
	public String put(String key, String value) {
		List<String> values = multiValuesMap.get(key);
		if (values == null) {
			values = new LinkedList<String>();
			multiValuesMap.put(key, values);
		}
		values.add(value);
		return super.put(key, value);
	}
	
}
