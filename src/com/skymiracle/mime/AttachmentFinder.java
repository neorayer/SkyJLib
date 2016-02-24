package com.skymiracle.mime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AttachmentFinder {

	/**
	 * @param args
	 * @throws IOException
	 */

	public static boolean findAttachment(String mailPath) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(mailPath));
			String line = null;
			while (null != (line = br.readLine())) {
				if (line.toLowerCase().indexOf("filename=\"") != -1)
					return true;
			}
			return false;
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
		}

	}

}
