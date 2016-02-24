package com.skymiracle.sor.render;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.exception.AppException;

public class AuthImageRenderer extends ImageRenderer {

	@Override
	public void render() {
		Image image = actResult.getAuthImage();
		try {
			response.setContentType("image/jpeg");
			ImageIO.write((RenderedImage) image, "JPG", response
					.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
