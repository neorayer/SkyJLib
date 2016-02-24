package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.skymiracle.fileBox.FileBox;
import com.skymiracle.fileBox.FileBoxImpl;
import com.skymiracle.fileBox.FileBoxLsItem;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UUID;

/**
 * 文件存储本地访问接口
 */
public class DocAccessorLocal extends IOAccessorLocal
		implements IDocAccessor {

	private FileBox docBox;

	public DocAccessorLocal(String username, String domain,
			String rootPath, HomeDirConfiger homeDirConfiger, String tmpDirPath)
			throws IOException {
		super(username, domain, "docbox", rootPath, homeDirConfiger, tmpDirPath);
		this.docBox = new FileBoxImpl(this.homePath);
	}

	public long docGetStorageSizeUsed() throws IOException {
		return this.docBox.df();
	}

	public List<String> docLsFldr(String folderPathInBox, boolean isIncSub)
			throws Exception {
		// 返回所有文件夹名,不包含子文件
		if (!isIncSub)
			return this.docBox.lsDirNames(folderPathInBox);
		else
			return this.docBox.lsDirPaths(folderPathInBox);
	}

	public void docNewFolder(String folderPathInBox, String newFolderName)
			throws Exception {
		String path = folderPathInBox + "/" + newFolderName;
		Logger.info("UserStorageDocAccessorLocal.docNewFolder " + path);
		this.docBox.mkDirIfNotExist(path);
	}

	public void docDelFolder(String folderPathInBox) throws Exception {
		Logger.info("UserStorageDocAccessorLocal.docDelFolder "
				+ folderPathInBox);
		this.docBox.rmDir(folderPathInBox);
	}

	public List<FileBoxLsItem> docLsFile(String folderPathInBox) throws Exception {
		return this.docBox.lsFileBoxItems(folderPathInBox);
	}

	public void docDelFiles(String[] folderPathInBoxs)
			throws Exception {
		for (String folderPathInBox : folderPathInBoxs) {
			if (folderPathInBox.length() == 0)
				continue;
			this.docBox.rmCommonFile(folderPathInBox);
		}
	}

	public void docStorFile(String folderPathInBox, String fileName,
			String tmpFilePath, boolean isMove) throws IOException {
		this.docBox.newCommonFile(folderPathInBox + "/" + fileName,
				tmpFilePath, isMove);

	}

	public void docStorFile(String folderPathInBox, String fileName,
			InputStream inputStream, boolean isClose) throws IOException {
		String tmpFilePath = this.tmpDirPath + "/" + new UUID().toShortString();
		StreamPipe.inputToFile(inputStream, tmpFilePath, isClose);
		docStorFile(folderPathInBox, fileName, tmpFilePath, true);
	}

	public void docMoveFilesTo(String[] filepaths,
			String destFolderPathInBox) throws Exception {

		int mvnum = 0;
		for (String filepath: filepaths) {
			if (filepath.trim().length() == 0)
				continue;

			String srcPathInBox = filepath;
			String destPathInBox = new File(destFolderPathInBox, new File(filepath).getName()).getAbsolutePath();
			if (destPathInBox.indexOf(":\\") == 1) {
				destPathInBox = destPathInBox.substring(2);
			}
			this.docBox.mvCommonFile(srcPathInBox, destPathInBox);
			mvnum++;
		}
	}

	public String docRetrFile(String folderPathInBox, String fileName)
			throws Exception {
		return this.docBox.getPathInFs(folderPathInBox + "/" + fileName);
	}

	public void docEmptyFolder(String pathInBox, boolean isIncSub)
			throws Exception {
		this.docBox.emptyDir(pathInBox, isIncSub);
	}

}
