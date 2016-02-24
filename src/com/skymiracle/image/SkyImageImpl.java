package com.skymiracle.image;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.skymiracle.util.FileTools;

public class SkyImageImpl implements SkyImage {

	private int width = 0;

	private int height = 0;

	private String imagePath = null;

	private String format = null;

	private int formatCode = SkyImage.FORMAT_JPG;

	private ImageReader ir = null;

	private BufferedImage bi = null;

	/**
	 * this constructor is used to operate an exiting image
	 * 
	 * @param imagePath
	 *            the path of image
	 * @param format
	 *            the type of image <see also>SkyImage.java
	 */
	public SkyImageImpl(String imagePath, int format) {
		this.imagePath = imagePath;
		this.format = getFormat(format);
		init();
	}

	public SkyImageImpl(File imageFile, int format) {
		this(imageFile.getAbsolutePath(), format);
	}

	/**
	 * this constructor is used to operate an exiting image, it will auto set
	 * format with filepath ext name.
	 * 
	 * @param imagePath
	 *            the path of image
	 */
	public SkyImageImpl(String imagePath) {
		this(imagePath, FileTools.getFileExt(imagePath));
	}

	public SkyImageImpl(String imagePath, String extName) {
		this.imagePath = imagePath;
		if (extName.equals("jpg")) {
			this.formatCode = SkyImage.FORMAT_JPG;
			this.format = getFormat(SkyImage.FORMAT_JPG);
		} else if (extName.equals("jpeg")) {
			this.formatCode = SkyImage.FORMAT_JPG;
			this.format = getFormat(SkyImage.FORMAT_JPG);
		} else if (extName.equals("bmp")) {
			this.formatCode = SkyImage.FORMAT_BMP;
			this.format = getFormat(SkyImage.FORMAT_BMP);
		} else if (extName.equals("gif")) {
			this.formatCode = SkyImage.FORMAT_GIF;
			this.format = getFormat(SkyImage.FORMAT_GIF);
		} else if (extName.equals("ico")) {
			this.formatCode = SkyImage.FORMAT_ICO;
			this.format = getFormat(SkyImage.FORMAT_ICO);
		} else if (extName.equals("png")) {
			this.formatCode = SkyImage.FORMAT_PNG;
			this.format = getFormat(SkyImage.FORMAT_PNG);
		} else if (extName.equals("tif")) {
			this.formatCode = SkyImage.FORMAT_TIF;
			this.format = getFormat(SkyImage.FORMAT_TIF);
		} else if (extName.equals("tiff")) {
			this.formatCode = SkyImage.FORMAT_TIF;
			this.format = getFormat(SkyImage.FORMAT_TIF);
		} else {
			this.formatCode = SkyImage.FORMAT_JPG;
			this.format = getFormat(SkyImage.FORMAT_JPG);
		}
		init();
	}

	/**
	 * this constructor is used to create a image
	 * 
	 * @param format
	 *            the type of image
	 */
	public SkyImageImpl(int format) {
		this.format = getFormat(format);
	}

	private String getFormat(int format) {
		switch (format) {
		case SkyImage.FORMAT_JPG:
			return "jpeg";
		case SkyImage.FORMAT_BMP:
			return "bmp";
		case SkyImage.FORMAT_GIF:
			return "gif";
		case SkyImage.FORMAT_ICO:
			return "ico";
		case SkyImage.FORMAT_PNG:
			return "png";
		case SkyImage.FORMAT_TIF:
			return "tif";
		default:
			return "unknow";
		}

	}

	private void init() {
		Iterator it = ImageIO.getImageReadersByFormatName(this.format);
		if (it.hasNext()) {
			this.ir = (ImageReader) it.next();
			try {
				this.ir.setInput(ImageIO.createImageInputStream(new File(
						this.imagePath)));
				this.bi = this.ir.read(0);
				this.width = this.bi.getWidth();
				this.height = this.bi.getHeight();
			} catch (IOException e) {
				System.out.println("setInput of ImageReader : " + e);
			}
		}
	}

	public int getImageWidth() {
		return this.width;
	}

	public int getImageHeight() {

		return this.height;
	}

	public void saveAs(String filePath, int format) throws IOException {
		saveAs(new File(filePath), format);

	}

	public void saveAs(File file, int format) throws IOException {
		ImageIO.write(this.bi, getFormat(format), file);

	}

	public void scale(double sx, double sy) {

		AffineTransform transform = new AffineTransform();
		transform.scale(sx, sy);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.bi = op.filter(this.bi, null);
	}

	public void scale(int width, int height) {
		double sx = (double) width / this.bi.getWidth();
		double sy = (double) height / this.bi.getHeight();
		this.scale(sx, sy);
	}

	public void scale(int size, boolean hv) {
		double times = 0.0;
		if (hv)
			times = (double) size / this.bi.getWidth();
		else
			times = (double) size / this.bi.getHeight();
		this.scale(times, times);
	}

	public void cut(int x, int y, int w, int h) {
		int bw = this.bi.getWidth();
		int bh = this.bi.getHeight();
		x = x < 0 ? 0 : x;
		y = y < 0 ? 0 : y;
		w = x + w > bw ? bw - x : w;
		h = y + h > bh ? bh - y : h;
		this.bi = this.bi.getSubimage(x, y, w, h);
	}

	public void regRotate(int angle) {
		AffineTransform transform = new AffineTransform();
		int w = this.width;
		int h = this.height;
		int max = this.height > this.width ? this.height : this.width;
		scale(max, max);
		transform.setToRotation(Math.toRadians(angle), max / 2, max / 2);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.bi = op.filter(this.bi, null);
		scale(h, w);
	}

	public void rotate(int angle) {
		double pi = 3.1415927;
		AffineTransform transform = new AffineTransform();
		transform.rotate(angle * pi / 180.0, this.bi.getWidth() / 2, this.bi
				.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.bi = op.filter(this.bi, null);

	}

	public Image getImage() {
		return this.bi;
	}

	public void bright(float brightness) {
		if (!this.bi.getColorModel().getColorSpace().isCS_sRGB()) {
			BufferedImage bimage = new BufferedImage(this.bi.getWidth(null),
					this.bi.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bimage.createGraphics();
			g.drawImage(this.bi, 0, 0, null);
			g.dispose();
			this.bi = bimage;
		}
		RescaleOp op = new RescaleOp(brightness, 0, null);
		this.bi = op.filter(this.bi, null);
	}

	public void text(String text, Font font, Color color, int x, int y) {
		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		BufferedImage scratchImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D scratchG2 = scratchImage.createGraphics();
		scratchG2.setRenderingHints(renderHints);
		FontRenderContext frc = scratchG2.getFontRenderContext();
		TextLayout tl = new TextLayout(text, font, frc);

		Graphics2D g2 = this.bi.createGraphics();
		g2.setRenderingHints(renderHints);

		g2.setColor(color);
		tl.draw(g2, x, y + (font.getSize()));

		scratchG2.dispose();
		scratchImage.flush();
		g2.dispose();

	}

	public void createAuthImage(String text, Font font, Color bgColor,
			Color textColor, double blurrness) {
		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		BufferedImage scratchImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D scratchG2 = scratchImage.createGraphics();
		scratchG2.setRenderingHints(renderHints);
		FontRenderContext frc = scratchG2.getFontRenderContext();
		TextLayout tl = new TextLayout(text, font, frc);
		Rectangle2D textBounds = tl.getBounds();
		int textWidth = (int) Math.ceil(textBounds.getWidth());
		int textHeight = (int) Math.ceil(textBounds.getHeight());
		int horizontalPad = 10;
		int verticalPad = 6;
		Dimension imageSize = new Dimension(textWidth + horizontalPad,
				textHeight + verticalPad);

		this.bi = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = this.bi.createGraphics();
		g2.setRenderingHints(renderHints);
		int baselineOffset = (verticalPad / 2) - ((int) textBounds.getY());
		g2.setColor(bgColor);
		g2.fillRect(0, 0, imageSize.width, imageSize.height);
		g2.setColor(textColor);
		tl.draw(g2, 5, baselineOffset);
		scratchG2.dispose();
		scratchImage.flush();
		g2.dispose();

		// set blurrness by bit
		int times = (int) (blurrness * (this.bi.getHeight() * this.bi
				.getWidth()));
		for (int i = 0; i < times; i++)
			this.bi.setRGB((int) (this.bi.getWidth() * Math.random()),
					(int) (this.bi.getHeight() * Math.random()), textColor
							.getRGB());

	}

	public void snapShot(int x, int y, int width, int height)
			throws AWTException {
		this.width = width;
		this.height = height;
		this.bi = (new Robot()).createScreenCapture(new Rectangle(x, y,
				this.width, this.height));
	}

	public void snapShot() throws AWTException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = (int) d.getWidth();
		this.height = (int) d.getHeight();
		snapShot(0, 0, this.width, this.height);
	}

	public static void printSupportImageFormats() {
		String[] mts = ImageIO.getReaderMIMETypes();
		for (String mt : mts)
			System.out.println(mt);
	}

	public String getImageFormat() {
		return format;
	}

	public int getImageFormatCode() {
		return formatCode;
	}

}
