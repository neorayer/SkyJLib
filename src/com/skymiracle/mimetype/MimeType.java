package com.skymiracle.mimetype;

import java.io.FileInputStream;
import java.util.Properties;

public class MimeType {
	private String extName;

	/**
	 * Init the MimeType ,get the file type
	 * 
	 * @param s.
	 *            s is a filename, filePath or fileExtname(no dot). egs.
	 *            example.jpg, c:\example.jpg, jpg.
	 */
	public MimeType(String s) {
		int index = 0;
		if ((index = s.lastIndexOf(".")) == -1)
			this.extName = s;
		else {
			this.extName = s.substring(index + 1);
		}
	}

	/**
	 * get the Content-Type
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getContentType() throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(
				"src/com/skymiracle/mimetype/mimetypes.properties"));
		return prop.getProperty(this.extName) == null ? "application/octet-stream"
				: prop.getProperty(this.extName);
	}


}
