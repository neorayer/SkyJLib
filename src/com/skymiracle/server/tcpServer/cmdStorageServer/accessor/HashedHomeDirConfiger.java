package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;

import com.skymiracle.util.FsHashPath;

public class HashedHomeDirConfiger implements HomeDirConfiger {

	private int hashDepth = 0;

	public int getHashDepth() {
		return this.hashDepth;
	}

	public HashedHomeDirConfiger() {
		this(0);
	}

	public HashedHomeDirConfiger(int hashDepth) {
		super();
		this.hashDepth = hashDepth;
	}

	public void setHashDepth(int hashDepth) {
		this.hashDepth = hashDepth;
	}

	private String getRelHomeDirPath(String username, String domain) {
		return new FsHashPath(domain, username).getDir(this.hashDepth)
				+ username;
	}

	public String getHomeDirPath(String home, String username, String domain,
			boolean isForceCreate) throws IOException {
		String dirPath = getRelHomeDirPath(username, domain);
		File file = new File(home, dirPath);
		if (!isForceCreate)
			return file.getAbsolutePath();
		if (file.isDirectory())
			return file.getAbsolutePath();
		if (!file.mkdirs()) {
			throw new IOException("Create dir " + dirPath + " failed.");
		}
		return file.getAbsolutePath();
	}

	@Override
	public String toString() {
		return "HashedHomeDirConfiger hashDepth=" + this.hashDepth;
	}

	public String getHashParentPath(String home, String prefix, String fname, boolean isForceCreate)
			throws IOException {
		String path = new FsHashPath(prefix , fname).getDir(this.hashDepth);
		File file = new File(home, path);
		if (isForceCreate) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static void main(String[] args) {
		String username = "zhourui";
		String domain = "skymiracle.com"; 
		HashedHomeDirConfiger conf = new HashedHomeDirConfiger();
		conf.setHashDepth(2);
		String r = conf.getRelHomeDirPath(username, domain);
		System.out.println(r);
	}
}
