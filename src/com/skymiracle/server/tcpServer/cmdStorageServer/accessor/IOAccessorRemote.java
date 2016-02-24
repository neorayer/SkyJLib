package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import com.skymiracle.logger.Logger;
import com.skymiracle.tcp.CRLFTerminatedReader;
import com.skymiracle.tcp.InternetPrintWriter;
import com.skymiracle.server.tcpServer.cmdServer.CmdServer;
import com.skymiracle.server.tcpServer.cmdServer.Commander;

/**
 * 存储远程访问接口 邮件“界面系统” 可能与 “文件存储系统”不在一台服务器上。所以需要通过TCP协议远程访问
 */
public class IOAccessorRemote {

	// 用户名
	protected String username;

	// 邮件域
	protected String domain;

	// 文件存储系统IP
	protected String host;

	// 文件存储系统端口
	protected int port;

	// 访问套接字
	protected Socket socket;

	// 输出流
	protected InternetPrintWriter lnWriter;

	// 输入流
	protected CRLFTerminatedReader lnReader;

	// 缓存文件目录
	protected String cacheDirPath = "/tmp/cache";

	// 缓存哈希带权
	protected int cacheHashDepth = 2;

	// 临时文件目录
	protected String tmpDirPath = "/tmp";

	public IOAccessorRemote(String username, String domain, String host,
			int port, String tmpDirPath, String cacheDirPath, int cacheHashDepth)
			throws IOException {
		this.username = username;
		this.domain = domain;
		this.host = host;
		this.port = port;
		this.tmpDirPath = tmpDirPath;
		this.cacheDirPath = cacheDirPath;
		this.cacheHashDepth = cacheHashDepth;
	}

	protected void openSocket() throws UnknownHostException, IOException {
		Logger.debug("UserStorageAccessRemoteImpl: openSocket: host:" + this.host + ", port:" + port);
		this.socket = new Socket(this.host, this.port);
		this.lnReader = new CRLFTerminatedReader(this.socket.getInputStream(),
				"ISO8859-1");
		this.lnWriter = new InternetPrintWriter(new BufferedOutputStream(
				this.socket.getOutputStream(), 1024), true);

		this.lnReader.readLine();
	}

	protected void closeSocket() throws IOException {
		Logger.debug("UserStorageAccessRemoteImpl: closeSocket");
		try {
			if (this.lnReader != null)
				this.lnReader.close();
		} catch (Exception e) {
			this.lnReader = null;
		}
		try {
			if (this.lnWriter != null)
				this.lnWriter.close();
		} catch (Exception e) {
			this.lnWriter = null;
		}
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (Exception e) {
			this.socket = null;
		}
	}

	protected void println(String s) {
		this.lnWriter.println(s);
		Logger.detail("UserStorageAccessRemoteImpl: -> " + s);
	}

	protected String readln() throws IOException {
		String s = this.lnReader.readLine();
		Logger.detail("UserStorageAccessRemoteImpl: <- " + s);
		return s;
	}

	protected String talkCmd(String cmd, String[] args) throws IOException {
		String paramsLine = "";
		for (String arg : args)
			paramsLine += arg + " ";
		String cmdLine = cmd + " " + this.username + " " + this.domain + " "
				+ paramsLine.trim();

		println(cmdLine);
		return readln();
	}

	protected String ocTalkCmd(String cmd, String[] args) throws IOException {
		try {
			openSocket();
			String paramsLine = "";
			for (String arg : args)
				paramsLine += arg + " ";
			String cmdLine = cmd + " " + this.username + " " + this.domain
					+ " " + paramsLine.trim();

			println(cmdLine);
			return readln();
		} catch (Exception e) {
			return e.getMessage();
		} finally {
			closeSocket();
		}
	}

	protected String talkCmd(Class<? extends Commander> cmdClass, String[] args)
			throws Exception {
		String cmd = CmdServer.getCommanderName(cmdClass);
		return talkCmd(cmd, args);
	}

	protected String ocTalkCmd(Class<? extends Commander> cmdClass,
			String[] args) throws Exception {
		String cmd = CmdServer.getCommanderName(cmdClass);
		return ocTalkCmd(cmd, args);
	}

	protected void checkNoPrefix2Exception(String s) throws Exception {
		if (s == null || !s.startsWith("2"))
			throw new Exception("The prefix of return code is not '2' but " + s);
	}

	protected String getUserCacheDirPath() throws IOException {
		HashedHomeDirConfiger dirConfiger = new HashedHomeDirConfiger(
				this.cacheHashDepth);
		return dirConfiger.getHomeDirPath(this.cacheDirPath, this.username,
				this.domain, true);
	}

	protected String getPubCachePath(String storageName, String fname)
			throws IOException {
		HashedHomeDirConfiger dirConfiger = new HashedHomeDirConfiger(
				this.cacheHashDepth);
		return dirConfiger.getHashParentPath(this.cacheDirPath, storageName,
				fname, true);
	}

}
