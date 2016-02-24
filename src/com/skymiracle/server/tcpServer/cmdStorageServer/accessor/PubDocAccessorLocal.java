package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.skymiracle.io.*;
import com.skymiracle.util.*;

public class PubDocAccessorLocal implements IPubDocAccessor {
	private HomeDirConfiger homeDirConfiger;
	private String localTmpDirPath;

	private String homePath;

	public PubDocAccessorLocal(String storageName, String localRootPath,
			HomeDirConfiger homeDirConfiger, String localTmpDirPath) {
		super();
		this.homeDirConfiger = homeDirConfiger;
		this.localTmpDirPath = localTmpDirPath;

		this.homePath = localRootPath + "/" + storageName;
		File file = new File(this.homePath);
		if (!file.exists())
			file.mkdirs();
	}

	private File getStorFile(String fname) throws IOException {
		return new File(this.homeDirConfiger.getHashParentPath(this.homePath,
				"files", fname, true));
	}

	public File retrFile(String fname) throws IOException {
		File hashDir = getStorFile(fname);
		File destFile = new File(hashDir, fname);
		return destFile;
	}

	public void storFile(File srcFile, String fname, boolean isMove)
			throws IOException {
		File hashDir = getStorFile(fname);
		File destFile = new File(hashDir, fname);
		if (isMove)
			FileTools.moveFile(srcFile, destFile);
		else
			FileTools.copyFile(srcFile, destFile);
	}

	public void storFile(InputStream is, String fname, boolean isClosed)
			throws IOException {
		File hashDir = getStorFile(fname);
		File destFile = new File(hashDir, fname);
		StreamPipe.inputToFile(is, destFile, isClosed);
	}

	public void deleteFile(String[] fnames) throws Exception {
		for (String fname : fnames) {
			fname = fname.trim();
			if (fname.length() == 0)
				continue;
			File hashDir = getStorFile(fname);
			File destFile = new File(hashDir, fname);
			destFile.delete();
		}
	}

}
