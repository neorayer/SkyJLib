package com.skymiracle.portal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class PagePart {

	private String tmpl;

	public String getTmpl() {
		return this.tmpl;
	}

	public void setTmpl(String tmpl) {
		this.tmpl = tmpl;
	}

	protected abstract void fillDataMap(Map<String, Object> dataMap)
			throws Exception;

	public String getResult() throws Exception {
		return getResult("UTF-8");
	}

	public String getResult(String charset) throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		fillDataMap(dataMap);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter out = new OutputStreamWriter(baos, charset);
		output(this.tmpl, dataMap, out);
		byte[] bs = baos.toByteArray();
		out.close();
		baos.close();
		String s = new String(bs, charset);
		return s;
	}

	protected abstract void output(String tmpl, Map<String, Object> dataMap,
			OutputStreamWriter out) throws Exception;

}
