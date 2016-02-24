package com.skymiracle.fileBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.skymiracle.io.Dir;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.FileTools;

/**
 * The implement of FileBox. AND, the MOST important implovement is a file named
 * '.size'. Any file modification what can change the size of file directoy,
 * will change the content of file '.size'. Then, we can get all the FileBox
 * size without scanning all the files size of the directory. The speed is high
 * very much.
 * 
 * @author neora
 * 
 */
public class FileBoxImpl implements FileBox {

	// 根目录物理路径
	private String rootPathInFs;

	//  根目录下.size文件物理路径
	private String sizePathInFs;

	// .size文件锁
	private Object sizeLock;

	public FileBoxImpl(String rootPathInFs) {
		this.rootPathInFs = rootPathInFs;
		this.sizePathInFs = this.rootPathInFs + "/.size";
		this.sizeLock = FileBoxSizeFileLockFactory
				.getSizeFileLock(this.rootPathInFs);
	}

	/**
	 * 查看.size文件内容（即邮箱已使用空间的大小）
	 */
	public long df() throws IOException {
		createSizeFileIfNotExist();
		try {
			String s = StreamPipe.fileToString(this.sizePathInFs, Charset.defaultCharset().name());
			return Long.parseLong(s.trim());
		} catch (Exception e) {
			Logger.error("size file df error. file = " + this.sizePathInFs + " reason:" + e.getMessage());
			return 0;
		}
	}
	
	private void createSizeFileIfNotExist() {
		File sizeFile = new File(this.sizePathInFs);
		// size file exists, return
		if(sizeFile.exists())
			return;
		// stat size parent file
		long size = statDirSize(sizeFile.getParentFile());
		try {
			sizeFile.createNewFile();
			StreamPipe.stringToFile("" + size, this.sizePathInFs, Charset.defaultCharset().name());
		} catch (IOException e) {
			Logger.error("size file create error. file = " + this.sizePathInFs + " reason:" + e.getMessage());
			e.printStackTrace();
		}
		Logger.info("create size file. file = " + this.sizePathInFs + ". size =" + size);
	}
	
	private static long statDirSize(File file) {
		if(!file.exists())
			return 0;
		if(file.isFile())
			return file.length();
		
		long l = 0;
		File[] fs = file.listFiles();
		for(File f: fs) {
			l += statDirSize(f);
		}
		return l;
	}

	/**
	 * 修改.size文件内容（即修改邮箱已使用空间的大小）
	 */
	protected void modSize(long l) throws IOException {
		synchronized (this.sizeLock) {
			long oldSize = df();
			long newSize = oldSize + l;
			StreamPipe.stringToFile("" + newSize, this.sizePathInFs, Charset.defaultCharset().name());
		}
	}

	public String getRootPathInFs() {
		return this.rootPathInFs;
	}

	/**
	 * 根据源文件，在指定路径中新建一个文件, 同时修改邮箱空间
	 * @param pathInBox 目标路径
	 * @param srcPathInFs 源文件路径
	 * @param isMove 是否把源文件移动目标路径
	 */
	public void newCommonFile(String pathInBox, String srcPathInFs,
			boolean isMove) throws IOException {
		String pathInFs = getPathInFs(pathInBox);
		pathInFs = getUniquePathInFs(pathInFs);
		if (isMove)
			FileTools.moveFile(srcPathInFs, pathInFs);
		else
			FileTools.copyFile(srcPathInFs, pathInFs);
		long fileSize = new File(pathInFs).length();
		modSize(fileSize);
	}

	/**
	 * 根据输入流，在指定路径中新建一个文件, 同时修改邮箱空间
	 * @param pathInBox 目标路径
	 * @param srcPathInFs 输入流
	 * @param isMove 是否关闭输入流
	 */
	public void newCommonFile(String pathInBox, InputStream is, boolean isClose)
			throws IOException {
		String pathInFs = getPathInFs(pathInBox);
		pathInFs = getUniquePathInFs(pathInFs);
		StreamPipe.inputToFile(is, pathInFs, isClose);
		long fileSize = new File(pathInFs).length();
		modSize(fileSize);
	}
	
	private String getUniquePathInFs(String pathInFs) {
		File file = new File(pathInFs);
		if(!file.exists())
			return pathInFs;
		
		String dirPath = file.getParent();
		String fileName = file.getName();
		String simpName = fileName;
		String extName = "";
		int index = fileName.lastIndexOf('.');
		if(index > 0)  {
			simpName = fileName.substring(0, index);
			extName = fileName.substring(index + 1);
		}
		if(!extName.equals(""))
			extName = "." + extName;
	
		int i = 1;
		while(true) {
			pathInFs = new StringBuffer(dirPath).append("/").append(simpName).append("(").append(i++).append(")").append(extName).toString();
			if(!new File(pathInFs).exists()){
				break;
			}
		}
		
		return pathInFs;
	}

	/**
	 * 读取一个指定路径的文件到输出流
	 * @param pathInBox 文件路径
	 * @param os 输出流
	 * @param isMove 是否关闭输出流
	 */
	public void readCommonFile(String pathInBox, OutputStream os,
			boolean isClose) throws IOException {
		String pathInFs = getPathInFs(pathInBox);
		StreamPipe.fileToOutput(pathInFs, os, isClose);
	}

	/**
	 * 删除一个指定路径的文件（该路径是相对于根目录的路径）
	 * @param pathInBox 文件路径
	 */
	public void rmCommonFile(String pathInBox) throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		rmCommonFileInFs(pathInFs);
	}

	/**
	 * 删除一个指定路径的文件（该路径是绝对路径）
	 * @param pathInBox 文件路径
	 */
	public void rmCommonFileInFs(String pathInFs) throws Exception {
		long fileSize = new File(pathInFs).length();
		boolean isSucc = new File(pathInFs).delete();
		if (!isSucc)
			throw new Exception("Can not remove file:" + pathInFs);
		modSize(-fileSize);
	}

	/**
	 * 移动一个源文件到目录路径
	 * @param srcPathInBox 源文件路径
	 * @param targetPathInBox 目标文件路径
	 */
	public void mvCommonFile(String srcPathInBox, String targetPathInBox)
			throws Exception {

		String srcPathInFs = getPathInFs(srcPathInBox);
		String targetPathInFs = getPathInFs(targetPathInBox);
		// TODO 用renameTo来达到移动文件，可能会有BUG
		boolean isSucc = new File(srcPathInFs)
				.renameTo(new File(targetPathInFs));
		if (!isSucc)
			throw new Exception("Can not rename file " + srcPathInFs + " to "
					+ targetPathInFs);
	}

	/**
	 * 复制一个源文件到目标路径
	 * @param srcPathInBox 源文件路径
	 * @param targetPathInBox 目标文件路径
	 */
	public void cpCommonFile(String srcPathInBox, String targetPathInBox)
			throws IOException {
		String srcPathInFs = getPathInFs(srcPathInBox);
		String targetPathInFs = getPathInFs(targetPathInBox);
		FileTools.copyFile(srcPathInFs, targetPathInFs);
		long fileSize = new File(targetPathInFs).length();
		modSize(fileSize);
	}

	/**
	 * 创建一个目录
	 * @param dirPathInBox 目录路径
	 */
	public void mkDir(String dirPathInBox) throws Exception {
		String pathInFs = getPathInFs(dirPathInBox);
		boolean isSucc = new File(pathInFs).mkdirs();
		if (!isSucc)
			throw new Exception("Can't mkdir " + pathInFs);
	}

	/**
	 * 创建一个目录, 如果目录不存在
	 * @param dirPathInBox 目录路径
	 */
	public void mkDirIfNotExist(String dirPathInBox) throws Exception {
		String pathInFs = getPathInFs(dirPathInBox);
		File file = new File(pathInFs);
		if (file.exists() && file.isDirectory())
			return;
		boolean isSucc = new File(pathInFs).mkdirs();
		if (!isSucc)
			throw new Exception("Can't mkdir " + pathInFs);
	}

	/**
	 * 删除一个目录
	 * @param dirPathInBox 目录路径
	 */
	public void rmDir(String dirPathInBox) throws Exception {
		String pathInFs = getPathInFs(dirPathInBox);
		File dir = new File(pathInFs);
		boolean isSucc = dir.delete();
		if (!isSucc)
			throw new Exception("Can't rmdir " + pathInFs);
	}

	/**
	 * 移动一个源目录文件到目标路径
	 * @param srcPathInBox 源文件路径
	 * @param targetPathInBox 目标文件路径
	 */
	
	public void mvDir(String srcPathInBox, String targetPathInBox)
			throws Exception {
		String srcPathInFs = getPathInFs(srcPathInBox);
		String targetPathInFs = getPathInFs(targetPathInBox);
		// TODO 用renameTo来达到移动文件，可能会有BUG
		boolean isSucc = new File(srcPathInFs)
				.renameTo(new File(targetPathInFs));
		if (!isSucc)
			throw new Exception("Can not rename file");
	}

	/**
	 * 列出该路径下的所有文件名
	 */
	public List<String> lsFileNames(String pathInBox) throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		Dir dir = new Dir(pathInFs);
		File[] files = dir.listFiles();
		if (files == null)
			throw new Exception("Can not list file");

		List<String> nameList = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				continue;
			String name = files[i].getName();
			nameList.add(name);
		}
		return nameList;
	}
	
	/**
	 * 清空该路径下的所有文件
	 * @param pathInBox 文件夹路径
	 * @param isIncSub 是否包含子文件夹
	 * @param exFileNames 
	 * @throws Exception
	 */
	public void emptyDir(String pathInBox, boolean isIncSub,
			String[] exFileNames) throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		Dir dir = new Dir(pathInFs);
		File[] files = dir.listFiles();
		if (files == null)
			throw new Exception("Can not list file");

		long delSize = 0;
		FOR_FILES: for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (isIncSub) {
					String subPathInBox = pathInBox + "/" + files[i].getName();
					emptyDir(subPathInBox, isIncSub);
					files[i].delete();
				}
				continue;
			}
			for (String exFileName : exFileNames) {
				if (exFileName.equals(files[i].getName())) {
					files[i].delete();
					continue FOR_FILES;
				}
			}
			delSize += files[i].length();
			files[i].delete();
		}
		modSize(-delSize);
	}

	public void emptyDir(String pathInBox, boolean isIncSub) throws Exception {
		emptyDir(pathInBox, isIncSub, new String[] { ".size" });
	}

	public List<FileInfo> lsFileInfos(String pathInBox) throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		Dir dir = new Dir(pathInFs);
		File[] files = dir.listFiles();
		if (files == null)
			throw new Exception("Can not list file");

		List<FileInfo> fileInfos = new LinkedList<FileInfo>();
		for (File file : files) {
			String name = file.getName();
			String filePathInBox = new StringBuffer(pathInBox).append("/")
					.append(name).toString();
			String filePathInFs = getPathInFs(filePathInBox);
			fileInfos.add(new FileInfo(filePathInFs, filePathInBox));
		}
		return fileInfos;
	}

	/**
	 * 某路径下所有的文件列表
	 */
	public List<FileBoxLsItem> lsFileBoxItems(String pathInBox)
			throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		Dir dir = new Dir(pathInFs);
		File[] files = dir.listFiles();
		if (files == null)
			throw new Exception("Can not list file");

		List<FileBoxLsItem> itemList = new LinkedList<FileBoxLsItem>();
		for (File file : files) {
			if (file.isDirectory())
				continue;
			String name = file.getName();
			if (name.equals(".size"))
				continue;
			long lastModified = file.lastModified();
			long size = file.length();
			String uuid = new File(pathInBox, name).getAbsolutePath();
			if (uuid.indexOf(":\\") == 1) {
				uuid = uuid.substring(2);
			}
			itemList.add(new FileBoxLsItem(uuid, name, size, lastModified));
		}
		return itemList;
	}

	/**
	 * 某个路径下的所有文件夹名，不包含子目录（注： 该路径是相对于根目录）
	 */
	public List<String> lsDirNames(String pathInBox) throws Exception {
		String pathInFs = getPathInFs(pathInBox);
		Dir dir = new Dir(pathInFs);
		File[] files = dir.listFiles();
		if (files == null)
			throw new Exception("Can not list file");

		List<String> dirNameList = new LinkedList<String>();
		for (File file : files) {
			if (!file.isDirectory())
				continue;
			dirNameList.add(file.getName());
		}
		return dirNameList;
	}

	/**
	 * 某个路径下的所有文件夹路径,包含子目录（注： 该路径是相对于根目录）
	 * @param pathInBox
	 * @param resList
	 * @throws Exception
	 */
	private void lsDirPaths(String pathInBox, List<String> resList)
			throws Exception {
		List<String> dirNames = this.lsDirNames(pathInBox);
		for (String dirName : dirNames) {
			String subPathInBox = pathInBox + "/" + dirName;
			resList.add(subPathInBox);
			lsDirPaths(subPathInBox, resList);
		}
	}

	/**
	 * 某个路径下的所有文件夹路径,包含子目录（注： 该路径是相对于根目录）
	 * @param pathInBox
	 * @param resList
	 * @throws Exception
	 */
	
	public List<String> lsDirPaths(String pathInBox) throws Exception {
		List<String> list = new LinkedList<String>();
		lsDirPaths(pathInBox, list);
		return list;
	}

	/**
	 * 根据相对于根目录的路径，找出文件的绝对路径
	 */
	public String getPathInFs(String pathInBox) {
		return new File(this.rootPathInFs, pathInBox).getAbsolutePath();
		// . new StringBuffer(this.rootPathInFs).append("/")
		// .append(pathInBox).toString();
	}

	public static void main(String[] args) throws Exception {
		Logger.info("begin");

		// FileInfo[] fileInfos = fileBox.lsFileInfos("/");
		// for (int i = 0; i < fileInfos.length; i++) {
		// fileInfos[i].length();
		// fileInfos[i].lastModified();
		// fileInfos[i].getName();
		// }
		// System.out.println(fileInfos.length);
		// Logger.info("1");
		// TextFile.loadLines("/tmp/1");
		Logger.info("end");

		//		
		// for (int i=5000; i<15000; i++) {
		// new File("/tmp/test/" + i).createNewFile();
		// }
		
		FileBoxImpl impl = new FileBoxImpl("d:\\impl");
		impl.emptyDir("box", false, new String[] { ".size", "cache.csv" });

	}

	public boolean exists(String pathInBox) {
		String pathInFs = getPathInFs(pathInBox);
		return new File(pathInFs).exists();
	}

	public void setUpdateFlag(String pathInBox, boolean isUpdate)
			throws Exception {
		String updateFlagFilePath = getPathInFs(pathInBox) + "/update";
		File updateFile = new File(updateFlagFilePath);
		if (!updateFile.exists()) {
			if (isUpdate)
				updateFile.createNewFile();
		} else if (!isUpdate)
			updateFile.delete();
	}

	public boolean isUpdate(String pathInBox) {
		String updateFlagFilePath = getPathInFs(pathInBox) + "/update";
		File updateFile = new File(updateFlagFilePath);
		if (updateFile.exists())
			return true;
		return false;
	}

	public File getFile(String pathInBox) {
		return new File(getPathInFs(pathInBox));
	}
}
