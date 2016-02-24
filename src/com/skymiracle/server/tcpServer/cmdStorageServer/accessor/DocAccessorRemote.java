package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.skymiracle.fileBox.FileBoxLsItem;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocDelFilesCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocDelFolderCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocEmptyFolderCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocLsFileCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocLsFldrCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocMoveFilesToCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocNewFolderCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocRetrFileCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocStorFileCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.UserDocStorageSizeUsedCommander;

/**
 * 文件存储远程访问接口
 */
public class DocAccessorRemote extends IOAccessorRemote
		implements IDocAccessor {

	public DocAccessorRemote(String username, String domain,
			String host, int port, String tmpDirPath, String cacheDirPath, int cacheHashDepth) throws IOException {
		super(username, domain, host, port, tmpDirPath,  cacheDirPath,  cacheHashDepth);
	}

	public void docDelFiles(String[] folderPathInBoxs) throws Exception {
		String s = "";
		for (String folderPathInBox : folderPathInBoxs) {
			s += folderPathInBox + "|";
		}
		String rs = ocTalkCmd(UserDocDelFilesCommander.class,
				new String[] { s });
		checkNoPrefix2Exception(rs);
	}

	public void docDelFolder(String folderPathInBox) throws Exception {
		String rs = ocTalkCmd(UserDocDelFolderCommander.class,
				new String[] { folderPathInBox });
		checkNoPrefix2Exception(rs);
	}

	public List<FileBoxLsItem> docLsFile(String folderPathInBox)
			throws Exception {
		List<FileBoxLsItem> itemList = new ArrayList<FileBoxLsItem>();
		try {
			openSocket();
			String s = talkCmd(UserDocLsFileCommander.class,
					new String[] { folderPathInBox });
			checkNoPrefix2Exception(s);
			println("ready");
			String content = StreamPipe.inputToString(this.socket
					.getInputStream(), "UTF-8", true);
			String[] lines = content.split("\r\n");
			for (String line : lines) {
				String[] ss = line.split("\t");
				if (ss.length != 3)
					continue;
				long size = Long.parseLong(ss[1]);
				long lastModified = Long.parseLong(ss[2]);
				String uuid = 	new File(
						folderPathInBox, ss[0]).getAbsolutePath();
					if (uuid.charAt(1) == ':')
						uuid = uuid.substring(2);

				FileBoxLsItem item = new FileBoxLsItem(uuid, ss[0], size,
						lastModified);
				itemList.add(item);
			}

		} finally {
			closeSocket();
		}
		return itemList;
	}

	/**
	 * 列出该路径下的所有文件夹
	 */
	public List<String> docLsFldr(String folderPathInBox, boolean isIncSub)
			throws Exception {
		List<String> folderList = new ArrayList<String>();
		try {
			openSocket();
			String s = talkCmd(UserDocLsFldrCommander.class, new String[] {
					folderPathInBox, "" + isIncSub });
			checkNoPrefix2Exception(s);
			println("ready");
			String content = StreamPipe.inputToString(this.socket
					.getInputStream(), "UTF-8", true);
			String[] lines = content.split("\r\n");
			for (String line : lines) {
				String folderName = line.trim();
				if (folderName.length() == 0)
					continue;
				folderList.add(folderName);
			}
		} finally {
			closeSocket();
		}
		return folderList;
	}

	public void docMoveFilesTo(String[] filepaths, String destFolderPathInBox)
			throws Exception {
		String filepathsStr = "";
		for (String filepath : filepaths)
			filepathsStr += filepath + "|";
		String rs = ocTalkCmd(UserDocMoveFilesToCommander.class, new String[] {
				filepathsStr, destFolderPathInBox });
		checkNoPrefix2Exception(rs);
	}

	public void docNewFolder(String folderPathInBox, String newFolderName)
			throws Exception {
		String rs = ocTalkCmd(UserDocNewFolderCommander.class, new String[] {
				folderPathInBox, newFolderName });
		checkNoPrefix2Exception(rs);
	}

	public String docRetrFile(String folderPathInBox, String fileName)
			throws Exception {
		String fname = 	"/" + folderPathInBox + "/" + fileName;
		File localFile = new File(getUserCacheDirPath() + fname);
		try {
			openSocket();
			String s = talkCmd(UserDocRetrFileCommander.class, new String[] {
					folderPathInBox, fileName });
			checkNoPrefix2Exception(s);
			println("ready");
			localFile.getParentFile().mkdirs();
			StreamPipe.inputToFile(this.socket.getInputStream(), localFile,
					true);
			return localFile.getAbsolutePath();
		} finally {
			closeSocket();
		}
	}

	public void docStorFile(String folderPathInBox, String fileName,
			String srcFilePath, boolean isMove) throws Exception {
		try {
			openSocket();
			String s = talkCmd(UserDocStorFileCommander.class, new String[] {
					folderPathInBox, fileName });
			checkNoPrefix2Exception(s);

			File srcFile = new File(srcFilePath);
			StreamPipe.fileToOutput(srcFile, this.socket.getOutputStream(),
					false);
			if (isMove)
				srcFile.delete();
		} finally {
			closeSocket();
		}
	}

	public void docEmptyFolder(String pathInBox, boolean isIncSub)
			throws Exception {
		String s = ocTalkCmd(UserDocEmptyFolderCommander.class, new String[] { pathInBox,
				"" + isIncSub });
		checkNoPrefix2Exception(s);
	}

	public long docGetStorageSizeUsed() throws Exception {
		String s = ocTalkCmd(UserDocStorageSizeUsedCommander.class,
				new String[] {});
		return Long.parseLong(s);
	}

}
