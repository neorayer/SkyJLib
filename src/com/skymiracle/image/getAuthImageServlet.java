package com.skymiracle.image;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.util.UUID;

public class getAuthImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String tmpDirPath = this.getInitParameter("tmpDirPath");
		String authCode = request.getParameter("authCode");

		String uuid = new UUID().toShortString();
		String imgPath = tmpDirPath + "/" + uuid + ".png";
		SkyImage img = new SkyImageImpl(SkyImage.FORMAT_PNG);

		img.createAuthImage(authCode, new Font("Arial", Font.BOLD, 24),
				Color.WHITE, Color.BLUE, 0.5);
		img.saveAs(imgPath, SkyImage.FORMAT_PNG);

		File file = new File(imgPath);

		response.reset();
		response.setContentType("image");
		StreamPipe.fileToOutput(file, response.getOutputStream(), false);

		file.delete();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response);
	}
}
