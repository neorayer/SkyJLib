package com.skymiracle.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MimeTypeAnalyserImpl implements MimeTypeAnalyser {

	private String type;

	private String majorType;

	private String minorType;

	private String extName;

	private static Properties mimeTypesPro;

	/**
	 * 
	 * @param name:
	 *            filePath, fileName or ExtName. e.gs, 'example.jpg',
	 *            'c:\tmp\example.jpg', '.jpg'.
	 */
	public MimeTypeAnalyserImpl(String name) {
		// Get Ext Name
		String fileName = (new File(name)).getName();
		int pos = fileName.lastIndexOf('.');
		if (pos < 0)
			this.extName = "";
		else
			this.extName = fileName.substring(pos + 1);

		// Load mimeTypes.properties resource.
		if (mimeTypesPro == null)
			try {
				loadMimeTypesPro();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// Analysis
		analysis();
	}

	private void loadMimeTypesPro() throws IOException {
		mimeTypesPro = new Properties();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				"com/skymiracle/mime/MimeTypes.properties");
		mimeTypesPro.load(is);
		is.close();
	}

	private void analysis() {
		this.type = mimeTypesPro.getProperty(this.extName);
		if (this.type == null)
			this.type = "unknown";
		int pos = this.type.indexOf('/');
		if (pos < 0) {
			this.majorType = this.type;
			this.minorType = "";
		} else {
			this.majorType = this.type.substring(0, pos);
			this.minorType = this.type.substring(pos + 1);
		}
	}

	public String getMajorType() {
		return this.majorType;
	}

	public String getMinorType() {
		return this.minorType;
	}

	public String getType() {
		return this.type;
	}

	public String getExt() {
		return this.extName;
	}

	/**
	 * Testcase
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		{
			MimeTypeAnalyser analyser = new MimeTypeAnalyserImpl("adsf.png");
			System.out.println(analyser.getType());
			System.out.println(analyser.getMajorType());
			System.out.println(analyser.getMinorType());
		}
		{
			MimeTypeAnalyser analyser = new MimeTypeAnalyserImpl(
					"/tmp/test/adsf.jpg");
			System.out.println(analyser.getType());
			System.out.println(analyser.getMajorType());
			System.out.println(analyser.getMinorType());
		}
		{
			MimeTypeAnalyser analyser = new MimeTypeAnalyserImpl(".gif");
			System.out.println(analyser.getType());
			System.out.println(analyser.getMajorType());
			System.out.println(analyser.getMinorType());
		}
	}

}
