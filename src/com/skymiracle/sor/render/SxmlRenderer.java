package com.skymiracle.sor.render;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.xml.XmlTools;

public class SxmlRenderer extends Renderer {

	@Override
	public void render() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		response.setContentType("text/xml;charset=" + request.getCharacterEncoding());

		Map<String, Object> dataMap = actResult.getDataMap();
		Collection<Object> dataColl = actResult.getDataColl();
		String s = "{}";
		if (dataColl != null) {
			s = XmlTools.getXml("datas", dataColl);
		} else {
			for (Object data : dataMap.values()) {
				if (data != null) {
					s = XmlTools.getXml(data);
				}
			}
		}
		response.getWriter().print(s);
	}

	@Override
	public void render(Exception e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}

	@Override
	public void render(AppException e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}
}
