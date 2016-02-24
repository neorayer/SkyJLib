package com.skymiracle.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Dir extends File {

	private static final long serialVersionUID = 5624862909487328322L;

	public static void main(String[] args) {
		System.out.println((new Dir("c:/server2.conf")).copyTo(new Dir(
				"c:/b/server2.conf")));
	}

	public Dir(String pathname) {
		super(pathname);
	}
	public Dir(File file) {
		super(file.getAbsolutePath());
	}
	
	public boolean copyTo(Dir dir) {
		if (!exists() || dir.exists())
			return false;
		String fatherPath = (dir.getAbsolutePath()).substring(0, dir
				.getAbsolutePath().lastIndexOf(File.separator));
		File file = new File(fatherPath);
		if (!file.exists() || file.isFile())
			return false;
		if (isFile()) {
			BufferedOutputStream fos = null;
			BufferedInputStream fis = null;
			try {
				fos = new BufferedOutputStream(new FileOutputStream(dir));
				fis = new BufferedInputStream(new FileInputStream(this));
				byte[] b = new byte[1024];
				int i = -1;
				while ((i = fis.read(b)) != -1)
					fos.write(b, 0, i);
				return true;
			} catch (Exception e) {
				if (dir.exists())
					dir.delete();
				return false;
			} finally {
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e) {
					} finally {
						fos = null;
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
					} finally {
						fis = null;
					}
			}
		} else {
			dir.mkdir();
			File[] files = listFiles();
			if (files.length == 0)
				return true;
			else {
				for (int i = 0; i < files.length; i++)
					new Dir(files[i].getAbsolutePath()).copyTo(new Dir(dir
							.getAbsolutePath()
							+ "/" + files[i].getName()));
				return true;
			}
		}
	}

	/**
	 * delete the file or the dir
	 */
	@Override
	public boolean delete() {
		if (!exists())
			return true;
		if (isFile())
			return delete();
		File[] files = listFiles();
		if (files == null)
			return true;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				files[i].delete();
			else
				(new Dir(files[i].getAbsolutePath())).delete();
		}
		return super.delete();
	}

	public boolean empty() {
		if (!exists())
			return true;
		if (isFile())
			return delete();
		File[] files = listFiles();
		if (files == null)
			return true;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				files[i].delete();
			else
				(new Dir(files[i].getAbsolutePath())).delete();
		}
		return true;
	}

	public boolean delAllFile() {
		if (!exists())
			return false;
		File[] files = listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				files[i].delete();
		}
		return true;
	}

	public void scanAll(FileDealWith fdw) throws IOException {
		if (!exists())
			throw new IOException("Dir does not exist.");
		if (!isDirectory())
			throw new IOException("Dir is not a directory");
		File[] files = listFiles();
		if (files == null)
			throw new IOException("Can not get files in dir.");
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				fdw.dealWith(files[i]);
			else if (files[i].isDirectory())
				(new Dir(files[i].getAbsolutePath())).scanAll(fdw);
		}
		fdw.dealWith(this);
	}

	public long size() {
		if (!exists())
			return 0;
		if (isFile())
			return length();
		File[] files = listFiles();
		long size = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				size += files[i].length();
			else
				size += (new Dir(files[i].getAbsolutePath())).size();
		}
		return size;
	}

	public int getFilesCount() {
		int c = 0;
		File[] files = listFiles();
		if (files == null)
			return -1;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				c++;
		}
		return c;
	}
}
