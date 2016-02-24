package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.util.List;

import com.skymiracle.fileBox.FileBoxLsItem;

/**
 * 文件存储访问接口
 */
public interface IDocAccessor {

	public long docGetStorageSizeUsed() throws Exception;

	public List<String> docLsFldr(String folderPathInBox, boolean isIncSub)
			throws Exception;

	public void docNewFolder(String folderPathInBox, String newFolderName)
			throws Exception;

	public void docDelFolder(String folderPathInBox) throws Exception;

	public List<FileBoxLsItem> docLsFile(String folderPathInBox)
			throws Exception;

	public void docDelFiles(String[] paths) throws Exception;

	public void docStorFile(String folderPathInBox, String fileName,
			String srcFilePath, boolean isMove) throws Exception;

	public void docMoveFilesTo(String[] filepaths, String destFolderPathInBox)
			throws Exception;

	public String docRetrFile(String folderPathInBox, String fileName)
			throws Exception;

	public void docEmptyFolder(String pathInBox, boolean isIncSub)
			throws Exception;

}
