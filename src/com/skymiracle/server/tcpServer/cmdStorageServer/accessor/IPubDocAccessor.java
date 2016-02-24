package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.File;

public interface IPubDocAccessor {

	public void storFile(File srcFile, String fname, boolean isMove)
			throws Exception;

	public File retrFile(String fname) throws Exception;

	public void deleteFile(String[] fnames) throws Exception;
}
