package com.skymiracle.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.util.Base64;

public class HttpResponseTools {

	public static void outDownload(HttpServletRequest request, HttpServletResponse response, File file,
			String filename, String charset, boolean isDelete) throws IOException {
		String clientAgent = request.getHeader("user-agent");
		boolean isIE = false;
		if (clientAgent.toLowerCase().indexOf("msie") >= 0)
			isIE = true;
		response.reset();
		response.setContentType("application/octet-stream");
		if (isIE){
//			filename = URLEncoder.encode(filename, charset);
//			filename = filename.replaceAll("[+]", "%20");
			String prefix = filename.lastIndexOf(".") != -1 ? filename
					.substring(0, filename.lastIndexOf(".")) : filename;
			String extension = filename.lastIndexOf(".") != -1 ? filename
					.substring(filename.lastIndexOf(".")) : "";
			String name = java.net.URLEncoder.encode(prefix, "UTF8");
			int limit = 190 - extension.length();
			if (name.length() > limit) {
				name = java.net.URLEncoder.encode(prefix.substring(0, Math.min(
						prefix.length(), limit / 9)), "UTF-8");
				if (name.lastIndexOf("%0A") != -1) {
					name = name.substring(0, name.length() - 3);
				}
			}
			filename = name + extension;   
			filename = filename.replaceAll("[+]", "%20");
		}else 
			filename = "=?" + charset +"?B?" + new String(Base64.encode(filename.getBytes(charset))) + "?=";
		response.addHeader("Content-Disposition", "attachment; filename=\""
				+filename  + "\"");

		if (file.exists()) {
			response.addHeader("Accept-Ranges", "bytes");
			response.addHeader("Content-length", "" + file.length());
			OutputStream out = response.getOutputStream();
			StreamPipe.fileToOutput(file, out, true);
			if (isDelete)
				file.delete();
		}
	}
}
