package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.IOException;

public interface HomeDirConfiger {

	public String getHomeDirPath(String home, String username, String domain,
			boolean isForceCreate) throws IOException;

	public String getHashParentPath(String home, String prefix,String fname,
			boolean isForceCreate) throws IOException;

}
