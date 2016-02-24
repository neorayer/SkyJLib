package com.skymiracle.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.skymiracle.logger.Logger;

/**
 * @author fatzhen
 * @create_date 2006-5-15
 */

public class Uncompress {

	private static void createDirectory(String directory, String subDirectory) {
		File fl = new File(directory);
		if (!"".equals(subDirectory)) {
			String[] dirs = subDirectory.replace('\\', '/').split("/");
			StringBuffer sb = new StringBuffer(subDirectory.length());
			for (int i = 0; i < dirs.length; i++) {
				sb.append(File.separator).append(dirs[i]);
				File subFile = new File(sb.toString());
				if (!subFile.exists())
					subFile.mkdir();
			}
		} else if (!fl.exists())
			fl.mkdir();
	}

	public static void main(String[] args) throws IOException {
		String a1 = "/home/fatzhen/a/3.sub.zip";
		String a2 = "/home/fatzhen/a/1.cn/";
		unZip(a1, a2);
	}

	public static void unZip(String zipFileName, String outputDirectory)
			throws IOException {
		ZipFile zipFile = new ZipFile(zipFileName);
		Enumeration e = zipFile.entries();
		// createDirectory(outputDirectory, "");
		new File(outputDirectory).mkdirs();
		while (e.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) e.nextElement();
			Logger.debug("unziping " + zipEntry.getName());

			String name = zipEntry.getName();
			String filePath = outputDirectory + "/" + name;
			File f = new File(filePath);
			if (zipEntry.isDirectory()) {
				name = name.substring(0, name.length() - 1);
				f.mkdir();
				Logger.debug("create dir: " + filePath);
			} else {
				String fileName = name.replace('\\', '/');
				if (fileName.indexOf("/") != -1)
					createDirectory(outputDirectory, fileName.substring(0,
							fileName.lastIndexOf("/")));
				f.createNewFile();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(f, false);
				byte[] bytes = new byte[1024];
				int c;
				while ((c = in.read(bytes)) != -1)
					out.write(bytes, 0, c);
				out.flush();
				out.close();
				in.close();
			}
		}
	}
}
