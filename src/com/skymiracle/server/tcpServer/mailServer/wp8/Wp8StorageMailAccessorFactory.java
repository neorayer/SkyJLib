package com.skymiracle.server.tcpServer.mailServer.wp8;

import com.skymiracle.auth.MailUser;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessorFactory;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IDocAccessor;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;

public class Wp8StorageMailAccessorFactory implements IMailAccessorFactory{

	public IDocAccessor getUserStorageDocAccessor(MailUser mailUser)
			throws Exception {
		throw new Exception("Not implemented");
	}

	public IMailAccessor getUserStorageMailAccessor(String username,
			String domain, String location) throws Exception {
		IMailAccessor uma = new Wp8MailAccessor(username, domain, location);
		return uma;
	}

	public IMailAccessor getUserStorageMailAccessor(MailUser mailUser)
			throws Exception {
		return getUserStorageMailAccessor(mailUser.getUid(), mailUser.getDc(),
				mailUser.getStorageLocation());
	}

}
