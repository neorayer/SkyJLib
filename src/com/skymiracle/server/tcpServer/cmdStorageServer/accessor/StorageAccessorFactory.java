package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;

public class StorageAccessorFactory implements IMailAccessorFactory,
		IDocAccessorFactory {

	public HomeDirConfiger homeDirConfiger = new HashedHomeDirConfiger();

	// 临时存储目录
	public String localTmpDirPath = "/tmp/";

	// （邮件/文件）存储根目录
	public String localRootPath = "/tmp/storage/";

	// 缓存 存储目录
	public String cacheDirPath = "/tmp/cache";

	public int cacheHashDepth = 2;

	public StorageAccessorFactory() {
	}

	public String getCacheDirPath() {
		return cacheDirPath;
	}

	public void setCacheDirPath(String cacheDirPath) {
		this.cacheDirPath = cacheDirPath;
	}

	public int getCacheHashDepth() {
		return cacheHashDepth;
	}

	public void setCacheHashDepth(int cacheHashDepth) {
		this.cacheHashDepth = cacheHashDepth;
	}

	public HomeDirConfiger getHomeDirConfiger() {
		return this.homeDirConfiger;
	}

	public void setHomeDirConfiger(HomeDirConfiger homeDirConfiger) {
		this.homeDirConfiger = homeDirConfiger;
	}

	public String getLocalRootPath() {
		return this.localRootPath;
	}

	public void setLocalRootPath(String localRootPath) {
		this.localRootPath = localRootPath;
	}

	public String getLocalTmpDirPath() {
		return this.localTmpDirPath;
	}

	public void setLocalTmpDirPath(String localTmpDirPath) {
		this.localTmpDirPath = localTmpDirPath;
	}

	// mail accessor
	
	public MailAccessorLocal getUserStorageMailAccessorLocal(String username,
			String domain) throws Exception {
		return new MailAccessorLocal(username, domain, this.localRootPath,
				this.homeDirConfiger, this.localTmpDirPath);
	}

	public IMailAccessor getUserStorageMailAccessor(MailUser<?> mailUser)
			throws Exception {
		return getUserStorageMailAccessor(mailUser.getUid(), mailUser.getDc(),
				mailUser.getStorageLocation());
	}

	public IMailAccessor getUserStorageMailAccessor(String username,
			String domain, String location) throws Exception {
		// TODO 把127.0.0.1作为本地，不知道会不会有问题？？？
		if (location.equals(Authable.LOCATION_NATIVE_LOCAL))
//		if (location.equals(Authable.LOCATION_NATIVE_LOCAL) || location.startsWith("127.0.0.1"))
			return getUserStorageMailAccessorLocal(username, domain);
		else if (location.equals(Authable.LOCATION_EXCEPTION))
			throw new Exception("User Location Exception");
		else if (location.equals(Authable.LOCATION_FOREIGN))
			throw new Exception("location can not be FOREIGN");

		String[] ss = location.split(":");
		if (ss.length != 2)
			throw new Exception("User Location Format Exception. location="
					+ location);

		String host = ss[0];
		int port = Integer.parseInt(ss[1]);
		return new MailAccessorRemote(username.trim(), domain.trim(), host,
				port, this.localTmpDirPath, this.cacheDirPath,
				this.cacheHashDepth);

	}
	
	// doc accessor

	public DocAccessorLocal getUserStorageDocAccessorLocal(String username,
			String domain) throws Exception {
		return new DocAccessorLocal(username, domain, this.localRootPath,
				this.homeDirConfiger, this.localTmpDirPath);
	}

	public IDocAccessor getUserStorageDocAccessor(MailUser<?> mailUser)
			throws Exception {
		return getUserStorageDocAccessor(mailUser.getUid(), mailUser.getDc(),
				mailUser.getStorageLocation());
	}

	public IDocAccessor getUserStorageDocAccessor(String username,
			String domain, String location) throws Exception {
		if (location.equals(Authable.LOCATION_NATIVE_LOCAL) || location.startsWith("127.0.0.1"))
			return getUserStorageDocAccessorLocal(username, domain);
		else if (location.equals(Authable.LOCATION_EXCEPTION))
			throw new Exception("User Location Exception");
		else if (location.equals(Authable.LOCATION_FOREIGN))
			throw new Exception("location can not be FOREIGN");

		String[] ss = location.split(":");
		if (ss.length != 2)
			throw new Exception("User Location Format Exception. location="
					+ location);

		String host = ss[0];
		int port = Integer.parseInt(ss[1]);
		return new DocAccessorRemote(username.trim(), domain.trim(), host,
				port, this.localTmpDirPath, this.cacheDirPath,
				this.cacheHashDepth);

	}
	
	// pub-doc accessor

	public IPubDocAccessor getPubDocStorageAccessor(String storageName,
			String location) throws Exception {
		if (location.equals(Authable.LOCATION_NATIVE_LOCAL))
			return getPubStorageDocAccessorLocal(storageName);
		else if (location.equals(Authable.LOCATION_EXCEPTION))
			throw new Exception("Public Location Exception");
		else if (location.equals(Authable.LOCATION_FOREIGN))
			throw new Exception("location can not be FOREIGN");

		String[] ss = location.split(":");
		if (ss.length != 2)
			throw new Exception("Location Format Exception. location="
					+ location);

		String host = ss[0];
		int port = Integer.parseInt(ss[1]);
		return new PubDocAccessorRemote(storageName, host, port,
				this.localTmpDirPath, this.cacheDirPath, this.cacheHashDepth);

	}

	public PubDocAccessorLocal getPubStorageDocAccessorLocal(String storageName) {
		return new PubDocAccessorLocal(storageName, this.localRootPath,
				this.homeDirConfiger, this.localTmpDirPath);
	}
}
