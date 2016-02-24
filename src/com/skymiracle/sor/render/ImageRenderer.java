package com.skymiracle.sor.render;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.exception.AppException;

public class ImageRenderer extends Renderer {

	@Override
	public void render() {
		Image image = actResult.getImage();
		try {
			String format = actResult.getImageFormat();
			if (format == null)
				format = "JPG";
			response.setContentType("image/" + format);
			if (image != null)
				ImageIO.write((RenderedImage) image, format, response
						.getOutputStream());
			else
				Logger.error("ImgRenderer: image is null!");
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
