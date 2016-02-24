package com.skymiracle.image;

import com.skymiracle.logger.Logger;

public class ScreenRecorder {
	static int serialNum = 0;

	private String imageFormat;

	private String defaultImageFormat = "png";

	private long interval = 100;

	private Screenshot cam;

	// public ScreenRecorder() {
	// this.fileName = this.defaultName;
	// this.imageFormat = this.defaultImageFormat;
	//
	// }

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	public void record(int count) throws Exception {
		long begin = System.currentTimeMillis();
		long now = begin;
		for (int i = 0; i < count; ) {
			now = System.currentTimeMillis();
			if (now - begin >= interval) {
				Logger.info("record frame " + i);
				cam.snapShot();
				begin = now;
				i++;
			}
		}

	}

	/**
	 * 
	 * @param s
	 * @param format
	 *            jpg or png
	 */
	public ScreenRecorder(String filePath, String format) {
		this.imageFormat = format;
		 cam = new Screenshot("/tmp/video1/", "png");//
	}

	public static void main(String[] args) throws Exception {
		ScreenRecorder cam = new ScreenRecorder("/tmp/video1/v_", "png");//

		cam.record(10);
	}
}