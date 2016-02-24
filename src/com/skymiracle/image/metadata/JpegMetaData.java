package com.skymiracle.image.metadata;

public class JpegMetaData {

	private int width;

	private int height;

	private String maker;

	private String model;

	private String iso;

	private String shutterSpeed;

	private String dateTimeDigitized;

	private String aperture;

	private String focalLength;

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAperture() {
		return this.aperture;
	}

	public void setAperture(String aperture) {
		this.aperture = aperture;
	}

	public String getDateTimeDigitized() {
		return this.dateTimeDigitized;
	}

	public void setDateTimeDigitized(String dateTimeDigitized) {
		this.dateTimeDigitized = dateTimeDigitized;
	}

	public String getFocalLength() {
		return this.focalLength;
	}

	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getIso() {
		return this.iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getMaker() {
		return this.maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getShutterSpeed() {
		return this.shutterSpeed;
	}

	public void setShutterSpeed(String shutterSpeed) {
		this.shutterSpeed = shutterSpeed;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
