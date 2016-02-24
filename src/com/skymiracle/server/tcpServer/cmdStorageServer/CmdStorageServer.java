package com.skymiracle.server.tcpServer.cmdStorageServer;

import com.skymiracle.server.tcpServer.cmdServer.CmdServer;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.StorageAccessorFactory;
/**
 *	存储命令服务
 */
public class CmdStorageServer extends CmdServer {
	private StorageAccessorFactory saFactory;

	public CmdStorageServer() throws Exception {
		super("CmdStorageServer", 6001);
		
		setWelcome("+OK WELCOME to SkyMiracle Storage Server.");
		setUnknown("-ERR Invalid command.");
		setShortConn(true);
		
		addCommander(PubDocRetrFileCommander.class);
		addCommander(PubDocStorFileCommander.class);
		addCommander(PubDocDelFilesCommander.class);
		
		addCommander(UserDocDelFilesCommander.class);
		addCommander(UserDocDelFolderCommander.class);
		addCommander(UserDocEmptyFolderCommander.class);
		addCommander(UserDocLsFileCommander.class);
		addCommander(UserDocLsFldrCommander.class);
		addCommander(UserDocMoveFilesToCommander.class);
		addCommander(UserDocNewFolderCommander.class);
		addCommander(UserDocRetrFileCommander.class);
		addCommander(UserDocStorFileCommander.class);
		addCommander(UserDocStorageSizeUsedCommander.class);

		addCommander(UserMailLsFldrCommander.class);
		addCommander(UserMailLsMailCommander.class);
		addCommander(UserMailDelMailCommander.class);
		addCommander(UserMailLsMailUUIDsizeCommander.class);
		addCommander(UserMailCopyMailCommander.class);
		addCommander(UserMailMoveMailCommander.class);
		addCommander(UserMailDelFolderCommander.class);
		addCommander(UserMailEmptyFldrCommander.class);
		addCommander(UserMailNewFolderCommander.class);
		addCommander(UserMailStorageSizeUsedCommander.class);
		addCommander(UserMailStorCommander.class);
		addCommander(UserMailSetReadCommander.class);
		addCommander(UserMailSetStarCommander.class);
		addCommander(UserMailSetReplyCommander.class);
		addCommander(UserMailSetLastModifiedCommander.class);
		addCommander(UserMailRetrCommander.class);
		addCommander(UserMailCreateAlertMailCommander.class);
		addCommander(UserMailGetFldrInfosCommander.class);
		addCommander(UserMailAddClassCommander.class);
		addCommander(UserMailRmClassCommander.class);
		addCommander(UserMailGetClassesCommander.class);
	}

	public CmdStorageServer(String name, int port, String homePath)
			throws Exception {
		this();
		setName(name);
		setPort(port);
	}

	public StorageAccessorFactory getSaFactory() {
		return this.saFactory;
	}

	public void setSaFactory(StorageAccessorFactory saFactory) {
		this.saFactory = saFactory;
	}

}
