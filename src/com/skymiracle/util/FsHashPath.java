package com.skymiracle.util;

import com.skymiracle.logger.Logger;

/**
 * FsHashPath implement HashPath interface, privade a serious of path hash
 * method. With it, you can get the hypodispersion for many diferrent string.
 * 
 * @author Zhourui
 * @date October,28,2005
 * 
 */
public class FsHashPath implements HashPath {
	private String name;

	private String rootPath;

	private String lastMd5Str;

	public FsHashPath(String rootPath, String name) {
		this.name = name;
		this.rootPath = rootPath;
	}

	public String getDir(int level) {
		Md5 md5 = new Md5(this.name);
		md5.processString();
		this.lastMd5Str = md5.getStringDigest();
		StringBuffer sb = new StringBuffer(this.rootPath);
		sb.append("/");
		if (level > this.lastMd5Str.length())
			level = this.lastMd5Str.length();
		for (int i = 0; i < level; i++)
			sb.append(this.lastMd5Str.substring(0, i + 1)).append("/");
		return sb.toString();
	}

	public String getPath(int level) {
		StringBuffer sb = new StringBuffer();
		String s = getDir(level);
		sb.append(s).append(this.lastMd5Str);
		return sb.toString();
	}

	/**
	 * TestCase
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.setLevel(Logger.LEVEL_DEBUG);
//		Logger.debug("begin");
//		{
//			String s = new FsHashPath("/test.com", "jiaohui").getDir(1);
//			System.out.println(s);
//		}
//		{
//			String s = new FsHashPath("/test.com", "zhourui").getPath(2);
//			System.out.println(s);
//		}
//		{
//			String s = new FsHashPath("/skymiracle.com", "tianliang").getPath(2);
//			System.out.println(s);
//		}
//		Logger.debug("End");
		String s = new FsHashPath("/test.com", "d1871d9a-2381-40aa-9001-c21a630f6c4c").getPath(2);
		System.out.println(s);
		
		// 
	}

}
