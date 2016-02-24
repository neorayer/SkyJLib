package com.skymiracle.portal;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class PortalPage {

	private String tmpl;

	protected Map<String, PagePart> partMap = new HashMap<String, PagePart>();

	public void addPagePart(PagePart pagePart, String posName) {
		this.partMap.put(posName, pagePart);
	}

	public void output(Writer out) throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();

		for (Map.Entry<String, PagePart> e : this.partMap.entrySet()) {
			PagePart pagePart = e.getValue();
			String posName = e.getKey();
			dataMap.put(posName, pagePart.getResult());
		}

		output(this.tmpl, dataMap, out);
	}

	protected abstract void output(String tmpl, Map dataMap, Writer out)
			throws Exception;

	public String getTmpl() {
		return this.tmpl;
	}

	public void setTmpl(String tmpl) {
		this.tmpl = tmpl;
	}
}
