package com.skymiracle.sor.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.util.Base64;

public class FileRenderer extends Renderer {

	@Override
	public void render() throws IOException {
		File file = actResult.getFile();
		InputStream is = new FileInputStream(file);
		if (is == null)
			throw new RuntimeException("您的actResult没有设置 InputStream!");
		String name = actResult.getFileName();
		long len = file.length();
		try {
			String clientAgent = request.getHeader("user-agent");
			String caLower = clientAgent.toLowerCase();
			boolean isIE = caLower.indexOf("msie") >= 0;
			boolean isFirefox = caLower.indexOf("firefox") >= 0;
			boolean isChrome = caLower.indexOf("chrome") >= 0;
			boolean isSafari = caLower.indexOf("safari") >= 0;
			boolean isOpera = caLower.indexOf("opera") >= 0;

			response.reset();
			response.setContentType("application/octet-stream");
			if (isFirefox || isChrome) {
				name = "=?UTF-8?B?" + Base64.encodeToString(name, "UTF-8")
						+ "?=";
			} else if (isIE) {
				name = new String(name.getBytes("GBK"), "ISO-8859-1");
			} else if (isSafari) {
				name = new String(name.getBytes("GBK"), "ISO-8859-1");
			} else if (isOpera) {
				name = new String(name.getBytes("UTF-8"), "ISO-8859-1");
			}

			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ name + "\"");
			response.addHeader("Accept-Ranges", "bytes");
			if (len >= 0)
				response.addHeader("Content-length", "" + len);
			StreamPipe.inputToOutput(is, response.getOutputStream(), false);

		} finally {
			is.close();
		}

	}

	@Override
	public void render(Exception e) throws Exception {
		Logger.error("", e);
	}

	@Override
	public void render(AppException e) throws IOException {
		Logger.error("", e);
	}

}
