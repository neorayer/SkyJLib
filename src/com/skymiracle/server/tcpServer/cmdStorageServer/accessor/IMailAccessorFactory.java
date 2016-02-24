package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import com.skymiracle.auth.MailUser;

public interface IMailAccessorFactory {

	public IMailAccessor getUserStorageMailAccessor(String username,
			String domain, String location) throws Exception;

	public IMailAccessor getUserStorageMailAccessor(MailUser<?> mailUser)
			throws Exception;
}
