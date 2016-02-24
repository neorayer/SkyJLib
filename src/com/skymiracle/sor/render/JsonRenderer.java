package com.skymiracle.sor.render;

import java.io.IOException;

import com.skymiracle.json.DNAJson;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.exception.AppException;

public class JsonRenderer extends Renderer {

	@Override
	public void render() throws Exception {
		response.setContentType("text/plain;charset=" + request.getCharacterEncoding());
		String s = "";
		if (actResult.getJson() != null) {
			s = DNAJson.getResJSONString(actResult.getJson());
		} else if (actResult.getJa() != null) {
			s = DNAJson.getResJSONString(actResult.getJa());
		} else if (actResult.getDao() != null) {
			s = DNAJson.getResJSONString(actResult.getDao(), actResult
					.getDesc());
		} else if (actResult.getMdo() != null) {
			s = DNAJson.getResJSONString(actResult.getMdo());
		} else if (actResult.getDataColl() != null) {
			s = DNAJson.getResJSONString(actResult.getDataColl(), actResult
					.getDesc());
		} else {
			s = DNAJson.getResJSONString(actResult.getDataMap());
		}
		response.getWriter().print(s);
	}

	@Override
	public void render(Exception e) throws IOException {
		response.setContentType("text/plain;charset=" + request.getCharacterEncoding());
		String s = DNAJson.getResJSONString(e);
		Logger.detail("", e);
		response.getWriter().print(s);
	}

	@Override
	public void render(AppException e) throws IOException {
		response.setContentType("text/plain;charset=" + request.getCharacterEncoding());
		String s = DNAJson.getResJSONString(e, false);
		Logger.detail("", e);
		response.getWriter().print(s);
	}
}
