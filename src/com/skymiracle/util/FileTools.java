package com.skymiracle.util;

import java.io.File;
import java.io.IOException;

import com.skymiracle.io.StreamPipe;

public class FileTools {
	/**
	 * Copies src file to dst file. If the dst file does not exist, it will be
	 * created
	 * 
	 * @throws IOException
	 * 
	 */
	public static void copyFile(String srcPath, String destPath)
			throws IOException {
		StreamPipe.fileToFile(srcPath, destPath);
	}

	public static void moveFile(String srcPath, String destPath)
			throws IOException {
		moveFile(new File(srcPath), new File(destPath));
	}

	public static void moveFile(File srcFile, File destFile) throws IOException {
		if (!srcFile.renameTo(destFile)) {
			copyFile(srcFile, destFile);
			srcFile.delete();
		}
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		StreamPipe.fileToFile(srcFile, destFile);
	}

	public static String getMimeType(File file) {
		String fileName = file.getName();
		int pos = fileName.lastIndexOf('.');
		if (pos < 0)
			return "application/octet-stream";
		String ext = fileName.substring(pos + 1);
		if (ext.equalsIgnoreCase("png"))
			return "image/png";
		else if (ext.equalsIgnoreCase("jpg"))
			return "image/jpeg";
		else if (ext.equalsIgnoreCase("jpeg"))
			return "image/jpeg";
		else if (ext.equalsIgnoreCase("jpe"))
			return "image/jpeg";
		else if (ext.equalsIgnoreCase("jpe"))
			return "image/jpeg";
		else if (ext.equalsIgnoreCase("gif"))
			return "image/gif";
		else if (ext.equalsIgnoreCase("tif"))
			return "image/tiff";
		else if (ext.equalsIgnoreCase("bmp"))
			return "image/bmp";
		else
			return "application/octet-stream";

	}

	public static String getFileExt(String s) {
		String s1; // = new String();
		int i = 0;
		int j = 0;
		if (s == null) {
			return null;
		}
		i = s.lastIndexOf('.') + 1;
		j = s.length();
		s1 = s.substring(i, j);
		if (s.lastIndexOf('.') > 0) {
			return s1.toLowerCase();
		} else {
			return "";
		}
	}

	public static String getContentType(String s) {
		String s1 = ""; // = new String();
		String s2 = ""; // = new String();
		int i = 0;
		// boolean flag = false;
		s1 = "Content-Type:";
		i = s.indexOf(s1) + s1.length();
		if (i != -1) {
			int j = s.length();
			s2 = s.substring(i, j);
		}
		return s2;
	}

	public static String getTypeMIME(String s) {
		// String s1 = new String();
		int i = 0;
		i = s.indexOf("/");
		if (i != -1) {
			return s.substring(1, i);
		} else {
			return s;
		}
	}

	public static String getSubTypeMIME(String s) {
		// String s1 = new String();
		// boolean flag = false;
		int i = 0;
		i = s.indexOf("/") + 1;
		if (i != -1) {
			int j = s.length();
			return s.substring(i, j);
		} else {
			return s;
		}
	}

	public static String getContentDisp(String s) {
		// String s1 = new String();
		String s1 = "";
		int i = 0;
		int j = 0;
		i = s.indexOf(":") + 1;
		j = s.indexOf(";");
		s1 = s.substring(i, j);
		return s1;
	}

}
