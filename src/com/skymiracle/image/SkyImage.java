package com.skymiracle.image;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public interface SkyImage {
	public final static int FORMAT_PNG = 10;
	public final static int FORMAT_JPG = 20;
	public final static int FORMAT_GIF = 30;
	public final static int FORMAT_TIF = 40;
	public final static int FORMAT_BMP = 50;
	public final static int FORMAT_ICO = 60;

	public int getImageWidth();

	public int getImageHeight();

	/**
	 * save an image to special filepath int a special format
	 * 
	 * @param filePath
	 * @param format
	 * @throws IOException
	 */
	public void saveAs(String filePath, int format) throws IOException;

	public void saveAs(File file, int format) throws IOException;

	/**
	 * maginify or shrink an image.
	 * 
	 * @param sx
	 *            the factor by which coordinates are scaled along the X axis
	 *            direction
	 * @param sy
	 *            the factor by which coordinates are scaled along the Y axis
	 *            direction
	 */
	public void scale(double sx, double sy);

	/**
	 * maginify or shrink to a special image which width,height is given.
	 * 
	 * @param new
	 *            image width
	 * @param new
	 *            image height
	 */
	public void scale(int width, int height);

	/**
	 * maginify or shrink to a special image which width or height is given.
	 * when hv is true which says width is given,otherwise height is given
	 * 
	 * @param new
	 *            image width
	 * @param new
	 *            image height
	 */
	public void scale(int size, boolean hv);

	/**
	 * rotate an image to special angle
	 * 
	 * @param angle
	 *            eg. 90, -90, 60, -30, 270, -45.
	 * 
	 */
	public void rotate(int angle);

	public void regRotate(int angle);

	/**
	 * get an image's buffer
	 * 
	 * @return ImageBuffer
	 */
	public Image getImage();

	/**
	 * change the image brightness
	 * 
	 * @param brightness
	 *            the specified scale factor
	 */
	public void bright(float brightness);

	/**
	 * draw some texts on an image.
	 * 
	 * @param text
	 *            being draw on the image
	 * @param font
	 *            the text font
	 * @param color
	 *            the text color
	 * @param x
	 *            the horizontal location of text
	 * @param y
	 *            the vertical location of text
	 */
	public void text(String text, Font font, Color color, int x, int y);

	/**
	 * create an image for auth
	 * 
	 * @param text:
	 *            the auth text
	 * @param font:
	 *            the text font
	 * @param bgColor:
	 *            the background color
	 * @param textColor:
	 *            the text color
	 * @param blurrness:
	 *            the blurring level ,value=[0,1]
	 */
	public void createAuthImage(String text, Font font, Color bgColor,
			Color textColor, double blurrness);

	
	public void snapShot(int x, int y, int width, int height)
			throws AWTException;

	public void snapShot() throws AWTException;
	
	public void cut(int x, int y, int w, int h);
	
	public String getImageFormat();
	public int getImageFormatCode();
}

