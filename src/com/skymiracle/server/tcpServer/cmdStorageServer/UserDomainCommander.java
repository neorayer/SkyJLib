package com.skymiracle.server.tcpServer.cmdStorageServer;

import java.io.UnsupportedEncodingException;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.cmdServer.ErrorCommanderClassException;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.PubDocAccessorLocal;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.DocAccessorLocal;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.MailAccessorLocal;

public abstract class UserDomainCommander extends AbsStorageCommander {

	public UserDomainCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		String[] args = tail.split(" +");
		if (args.length < 2)
			return getBytesCRLF("500 ERROR: username domain args...");

		String username = args[0];
		String domain = args[1];
		int pos = tail.indexOf(" " + domain) + (" " + domain).length();
		String lastStr = tail.substring(pos).trim();

		return doCmd(username, domain, lastStr);
	}

	protected DocAccessorLocal getUsDocAccessorLocal(
			String username, String domain) throws Exception {
		return this.storageServer.getSaFactory()
				.getUserStorageDocAccessorLocal(username, domain);
	}

	protected MailAccessorLocal getUsMailAccessorLocal(
			String username, String domain) throws Exception {
		return this.storageServer.getSaFactory()
				.getUserStorageMailAccessorLocal(username, domain);
	}

	protected PubDocAccessorLocal getPubDocAccessorLocal(
			String storageName) throws Exception {
		return this.storageServer.getSaFactory()
				.getPubStorageDocAccessorLocal(storageName);
	}

	@Override
	protected byte[] getHelpBytesCRLF(String s)
			throws UnsupportedEncodingException, ErrorCommanderClassException {
		return getBytes("550 " + getCommanderName() + " username domain " + s
				+ "\r\n");
	}

	protected abstract byte[] doCmd(String username, String domain, String tail)
			throws Exception;

}
