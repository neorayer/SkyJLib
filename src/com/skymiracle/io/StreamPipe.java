package com.skymiracle.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;

import com.skymiracle.logger.Logger;

public class StreamPipe {

	public static void inputToOutput(InputStream is, OutputStream os,
			boolean isClose) throws IOException {
		inputToOutput(is, isClose, os, isClose);
	}
	public static void inputToOutput(InputStream is, boolean inIsClose, OutputStream os,
			boolean outIsClose) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is, 1024);
		BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
		byte[] bytes = new byte[4096];
		int readSize = -1;
		try {
			while ((readSize = bis.read(bytes)) >= 0) {
				bos.write(bytes, 0, readSize);
			}
			bos.flush();
		} finally {
			if (inIsClose) {
				close(bis);
			}
			if (outIsClose) {
				close(bos);
			}
		}
	}

	public static void close(Closeable obj) {
		try {
			obj.close();
		} catch (IOException e) {
			Logger.warn("", e);
		}
	}

	public static void fileToOutput(String filePath, OutputStream os,
			boolean isClose) throws IOException {
		toOutput(new File(filePath), os, isClose);
	}

	public static void fileToOutput(File file, OutputStream os, boolean isClose)
			throws IOException {
		toOutput(file, os, isClose);
	}

	public static void toOutput(File file, OutputStream os, boolean isClose)
			throws IOException {
		FileInputStream fis = new FileInputStream(file);
		inputToOutput(fis, os, isClose);
		fis.close();

	}

	public static void inputToFile(InputStream is, File file, boolean isClose)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		inputToOutput(is, fos, isClose);
		fos.close();
	}

	public static void inputToFile(InputStream is, String filePath,
			boolean isClose) throws IOException {
		inputToFile(is, new File(filePath), isClose);
	}

	public static void bytesToOutput(byte[] bytes, OutputStream os,
			boolean isClose) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
		bos.write(bytes);
		bos.flush();
		if (isClose) {
			bos.close();
		}
	}

	public static void bytesToOutput(byte[] bytes, int len, OutputStream os,
			boolean isClose) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
		bos.write(bytes, 0, len);
		bos.flush();
		if (isClose) {
			bos.close();
		}
	}

	public static void bytesToFile(byte[] bytes, int len, File file)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		bytesToOutput(bytes, len, fos, true);
		fos.close();
	}

	public static void stringsToOutput(String[] strings, OutputStream os,
			boolean isClose) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
		for (int i = 0; i < strings.length; i++) {
			bos.write(strings[i].getBytes());
			bos.write("\r\n".getBytes());
		}
		bos.flush();
		if (isClose) {
			bos.close();
		}
	}

	public static byte[] inputToBytes(InputStream is, boolean isClose)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		inputToOutput(is, bos, isClose);
		return bos.toByteArray();
	}

	public static byte[] fileToBytes(File file) throws FileNotFoundException,
			IOException {
		return inputToBytes(new FileInputStream(file), true);
	}

	public static String inputToString(InputStream is, String charset,
			boolean isClose) throws IOException {
		return new String(inputToBytes(is, isClose), charset);
	}

	public static void fileToFile(String srcFilePath, String destFilePath)
			throws IOException {
		fileToFile(new File(srcFilePath), new File(destFilePath));
	}

	public static void fileToFile(File srcFile, File destFile)
			throws IOException {
		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		inputToOutput(fis, fos, true);
	}

	public static void objectToOutput(Object object, OutputStream outputStream,
			boolean isClose) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		oos.writeObject(object);
		oos.flush();
		if (isClose)
			oos.close();
	}

	public static Object inputToObject(InputStream inputStream, boolean isClose)
			throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		return ois.readObject();
	}

	public static Object fileToObject(File file) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		return inputToObject(fis, true);
	}

	public static void objectToFile(Object obj, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		objectToOutput(obj, fos, true);
	}

	public static String fileToString(String filePath, String charset)
			throws IOException {
		FileInputStream fis = new FileInputStream(filePath);
		return inputToString(fis, charset, true);
	}

	public static String fileToString(File file, String charset)
			throws IOException {
		return fileToString(file.getAbsolutePath(), charset);
	}

	public static void stringToFile(String s, File file, String charset)
			throws FileNotFoundException, IOException {
		byte[] bs = s.getBytes(charset);
		StreamPipe.bytesToOutput(bs, new FileOutputStream(file), true);
	}

	public static void stringToOutput(String s, OutputStream os,
			String charset, boolean isClosed) throws IOException {
		byte[] bs = s.getBytes(charset);
		StreamPipe.bytesToOutput(bs, os, true);
	}

	public static String urlToString(String urlStr, String charset)
			throws IOException {
		URL url = new URL(urlStr);
		return inputToString(url.openStream(), charset, true);
	}

	public static void urlToFile(String urlStr, String filepath)
			throws IOException {
		URL url = new URL(urlStr);
		inputToFile(url.openStream(), filepath, true);
	}

	public static void stringToFile(String string, String filepath,
			String charset) throws FileNotFoundException, IOException {
		stringToFile(string, new File(filepath), charset);

	}

}
