package com.skymiracle.fileBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * FileBox is class to read or write the files in a file directory. The file
 * directory like a container of files. There is a special path name
 * 'pathInBox'. pathInBox is different with 'pathInFs' --- path in filesystem.
 * 
 * @author neora
 * 
 */
public interface FileBox {

	public long df() throws IOException;

	public String getRootPathInFs();

	public void newCommonFile(String pathInBox, String srcPathInFs,
			boolean isMove) throws IOException;

	public void rmCommonFile(String pathInBox) throws Exception;

	public void newCommonFile(String pathInBox, InputStream is, boolean isClose)
			throws IOException;

	public void readCommonFile(String pathInBox, OutputStream os,
			boolean isClose) throws IOException;

	public void mvCommonFile(String srcPathInBox, String targetPathInBox)
			throws Exception;

	public void cpCommonFile(String srcPathInBox, String targetPathInBox)
			throws IOException;

	public void mkDir(String dirPathInBox) throws Exception;
	
	public void mkDirIfNotExist(String dirPathInBox) throws Exception;

	public void rmDir(String dirPathInBox) throws Exception;

	public void mvDir(String srcPathInBox, String targetPathInBox)
			throws Exception;

	public List<FileInfo> lsFileInfos(String pathInBox) throws Exception;

	public List<String> lsFileNames(String pathInBox) throws Exception;

	public List<FileBoxLsItem> lsFileBoxItems(String pathInBox) throws Exception;

	public String getPathInFs(String pathInBox);

	public File getFile(String pathInBox);

	public List<String> lsDirNames(String pathInBox) throws Exception;

	public List<String> lsDirPaths(String pathInBox) throws Exception;

	public boolean exists(String pathInBox);

	public void emptyDir(String pathInBox, boolean isIncSub) throws Exception;

	public boolean isUpdate(String pathInBox);

	public void setUpdateFlag(String string, boolean b) throws Exception;

}
