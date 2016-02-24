package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;
import java.io.IOException;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.server.tcpServer.cmdStorageServer.PubDocDelFilesCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.PubDocRetrFileCommander;
import com.skymiracle.server.tcpServer.cmdStorageServer.PubDocStorFileCommander;

public class PubDocAccessorRemote extends IOAccessorRemote implements
		IPubDocAccessor {

	String storageName;

	public PubDocAccessorRemote(String storageName, String host, int port,
			String tmpDirPath, String cacheDirPath, int cacheHashDepth)
			throws IOException {
		super("pub", "pub", host, port, tmpDirPath, cacheDirPath,
				cacheHashDepth);
		this.storageName = storageName;
	}

	public File retrFile(String fname) throws Exception {
		File localFile = new File(getPubCachePath(storageName, fname), fname);
		localFile.getParentFile().mkdirs();
		try {
			openSocket();
			String s = talkCmd(PubDocRetrFileCommander.class, new String[] {
					storageName, fname });
			checkNoPrefix2Exception(s);
			println("ready");
			localFile.getParentFile().mkdirs();
			StreamPipe.inputToFile(this.socket.getInputStream(), localFile,
					true);
			return localFile;
		} finally {
			closeSocket();
		}
	}

	public void storFile(File srcFile, String fname, boolean isMove)
			throws Exception {
		try {
			openSocket();
			String s = talkCmd(PubDocStorFileCommander.class, new String[] {
					this.storageName, fname });
			checkNoPrefix2Exception(s);

			StreamPipe.fileToOutput(srcFile, this.socket.getOutputStream(),
					false);
			if (isMove)
				srcFile.delete();
		} finally {
			closeSocket();
		}

	}

	public void deleteFile(String[] fnames) throws Exception {
		String s = "";
		for (String fname : fnames)
			s += fname + "|";
		this.ocTalkCmd(PubDocDelFilesCommander.class, new String[] {
				this.storageName, s });
	}

}
