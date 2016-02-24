package com.skymiracle.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Screenshot {
	private String fileName; // �ļ���ǰ׺

	private String defaultName = "/tmp/ScreenShot";

	static int serialNum = 0;

	private String imageFormat; // ͼ���ļ��ĸ�ʽ

	private String defaultImageFormat = "png";

	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

	public Screenshot() {
		this.fileName = this.defaultName;
		this.imageFormat = this.defaultImageFormat;

	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	/**
	 * 
	 * @param s
	 * @param format
	 *            jpg or png
	 */
	public Screenshot(String s, String format) {

		this.fileName = s;
		this.imageFormat = format;
	}

	public File snapShot() throws Exception {
		BufferedImage screenshot = (new Robot())
				.createScreenCapture(new Rectangle(0, 0, (int) this.d
						.getWidth(), (int) this.d.getHeight()));
		serialNum++;
		String name = this.fileName + String.valueOf(serialNum) + "."
				+ this.imageFormat;
		File f = new File(name);
		ImageIO.write(screenshot, this.imageFormat, f);
		return f;

	}

	public static void main(String[] args) throws Exception {
		Screenshot cam = new Screenshot();//

		File f = cam.snapShot();
		System.out.println(f.getAbsolutePath());
	}
}