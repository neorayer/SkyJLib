package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import com.skymiracle.auth.MailUser;

public interface IDocAccessorFactory {

	public IDocAccessor getUserStorageDocAccessor(String username,
			String domain, String location) throws Exception;

	public IDocAccessor getUserStorageDocAccessor(MailUser<?> mailUser)
			throws Exception;
}
